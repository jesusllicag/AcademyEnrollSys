package domain.model;

public class Professor {
    private String code;
    private String name;
    private String lastname;
    private String email;

    public Professor() {
    }

    public Professor(String code, String name, String lastname, String email) {
        this.code = code;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s %s | Email: %s", code, lastname, name, email);
    }
}
