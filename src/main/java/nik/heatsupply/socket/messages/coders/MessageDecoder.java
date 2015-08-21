package nik.heatsupply.socket.messages.coders;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import nik.heatsupply.socket.messages.CommandMessage;
import nik.heatsupply.socket.messages.Message;

public class MessageDecoder implements Decoder.Text<Message> {

	@Override
	public void destroy() {
		
	}

	@Override
	public void init(EndpointConfig config) {
		
	}

	@Override
	public Message decode(String s) throws DecodeException {
		Message message = null;
		JsonObject jsonObject = Json.createReader(new StringReader(s)).readObject();
		
		switch (jsonObject.getString("type").toLowerCase()) {
		case "commandmessage":
			CommandMessage cm = new CommandMessage();
			cm.setCommand(jsonObject.getString("command"));
			
			Map<String, String> parameters = new HashMap<>();
			JsonArray params = jsonObject.getJsonArray("parameters");
			
			if (params != null) {
				for (int i = 0; i < params.size(); i++) {
					JsonObject param = params.getJsonObject(i);
					Iterator<String> iterator = param.keySet().iterator();
					while (iterator.hasNext()) {
						String key = iterator.next();
						String value = param.get(key).toString();

						parameters.put(key, value.substring(1, value.length() - 1));
					}
				}
				cm.setParameters(parameters);
			}
			message = cm;
			break;
		}
		return message;
	}

	@Override
	public boolean willDecode(String s) {
		try {
			Json.createReader(new StringReader(s)).readObject();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
