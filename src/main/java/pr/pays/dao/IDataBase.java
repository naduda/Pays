package pr.pays.dao;

import pr.pays.model.User;

public interface IDataBase {
	Integer getMaxUserId();
	String addUser(String login, String password, String phone, String email, 
			int languageid, String owneraccount, String meterId, int typeDevice, String lastcash);
	boolean updateUser(int idUser, String password, String phone, String email, int languageid,
						String name, String middlename, String surname);
	boolean updateUserAttempts(int idUser, int attempts);
	boolean deleteUser(int idUser);
	boolean deleteUserFromDB(int idUser);
	User getUser(int idUser);
}