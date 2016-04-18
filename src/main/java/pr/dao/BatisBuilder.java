package pr.dao;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class BatisBuilder {
	private BatisImpl batis;
	
	public BatisBuilder (DataSource dataSource) {
		batis = new BatisImpl(dataSource);
	}
	
	public BatisBuilder addMappers(Class<?> mapper) {
		if(this.batis.getConfiguration() != null) {
			this.batis.getConfiguration().addMapper(mapper);
		}
		return this;
	}
	
	public BatisImpl build() {
		this.batis.setSqlSessionFactory(new SqlSessionFactoryBuilder()
				.build(this.batis.getConfiguration()));
		return this.batis;
	}
}