package nik.heatsupply.socket.messages.coders;

import java.util.Iterator;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import nik.heatsupply.socket.messages.CommandMessage;
import nik.heatsupply.socket.messages.Message;

public class MessageEncoder extends AEncoder {
	@Override
	public String encodeImpl(Object mess) {
		Message message = (Message)mess;
		String result = null;
		if (message instanceof CommandMessage) {
			CommandMessage commandMessage = (CommandMessage) message;
			JsonArrayBuilder params = Json.createArrayBuilder();
			Iterator<String> iterator = commandMessage.getParameters().keySet().iterator();

			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				String par = commandMessage.getParameters().get(key);
				JsonObjectBuilder param = Json.createObjectBuilder();
				param.add(key, par);
				params.add(param);
			}

			result = Json.createObjectBuilder().add("type", commandMessage.getClass().getSimpleName())
					.add("command", commandMessage.getCommand())
					.add("parameters", params).build().toString();
		}

		return result;
	}
}