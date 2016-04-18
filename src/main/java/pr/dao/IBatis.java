package pr.dao;

import java.rmi.RemoteException;
import org.apache.ibatis.session.SqlSession;

public interface IBatis {
	Object getResult(SqlSession session) throws RemoteException;
}