package pr.pays.model;

public interface IUser {
	User findUser(int idUser);
	User findUserByName(String name);
}