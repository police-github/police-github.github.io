package io.github.police_github.pojo;

public class Cop {
	private String name;

	private String position;

	private boolean auxiliary;

	private String source;

	public Cop() {
	}

	public Cop(String name, String position, boolean auxiliary, String source) {
		this.name = name;
		this.position = position;
		this.auxiliary = auxiliary;
		this.source = source;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public boolean isAuxiliary() {
		return auxiliary;
	}

	public void setAuxiliary(boolean auxiliary) {
		this.auxiliary = auxiliary;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Override
	public String toString() {
		return "Cop [name=" + name + ", position=" + position + ", auxiliary=" + auxiliary + ", source=" + source + "]";
	}

}
