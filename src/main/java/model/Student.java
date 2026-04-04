package model;

public class Student {
    private int id;
    private String name;
    private String email;
    private String contact;
    private String profilePic;

    // Constructor
    public Student(int id, String name, String email, String contact) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.contact = contact;
    }

    // Getters & Setters
    public int getId() { return id; }
    public String getName() { return name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
}