package test;

import com.jtk.persistence.JtkPersistenceException;
import com.jtk.persistence.JtkPersistence;

public class Test {

	public static void main(String[] args) throws JtkPersistenceException {
		TestBean bean = (TestBean) JtkPersistence.getInstance(TestBean.class);
		System.out.println(bean.toString());
		bean.setAaa(321);
		bean.setB(null);
		JtkPersistence.save(bean);
	}

}
