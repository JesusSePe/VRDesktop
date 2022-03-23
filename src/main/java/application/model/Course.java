package application.model;


public class Course {
	
	private String titulo;
	private String descripcion;
	
	public Course() {
		this(null, null);
	}
	
	public Course(String titulo, String descripcion) {
		this.titulo = titulo;
		this.descripcion = descripcion;
	}

	public String getTitulo() {
		return titulo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setTitulo(String titulo) {
		this.titulo = new String(titulo);
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = new String(descripcion);
	}
	
	public String tituloProperty() {
		return titulo;
	}
	
	public String descripcionProperty() {
		return descripcion;
	}

}
