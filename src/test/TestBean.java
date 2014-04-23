package test;

import com.jtk.persistence.JtkEntity;
import com.jtk.persistence.JtkKey;
import com.jtk.persistence.JtkNone;

@JtkEntity(name = "bin/test.properties")
public class TestBean {

	@JtkKey(name = "a")
	private int aaa;
	private String b;
	@JtkNone
	private String c;

	public int getAaa() {
		return aaa;
	}

	public void setAaa(int aaa) {
		this.aaa = aaa;
	}

	public String getB() {
		return b;
	}

	public void setB(String b) {
		this.b = b;
	}

	@Override
	public String toString() {
		return "TestBean [aaa=" + aaa + ", b=" + b + ", c=" + c + "]";
	}

}
