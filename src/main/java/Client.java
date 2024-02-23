public class Client {
    int id_client;
    String fio;
    String passport;

    public Client(String fio, String passport) throws Exception {
        if (passport.length() != 9 || !Character.isDigit(passport.charAt(3)) || !Character.isDigit(passport.charAt(4)) || !Character.isDigit(passport.charAt(5)) || !Character.isDigit(passport.charAt(6)) || !Character.isDigit(passport.charAt(7)) || !Character.isDigit(passport.charAt(8)) || !Character.isSpaceChar(passport.charAt(2)) || !Character.isUpperCase(passport.charAt(0)) || !Character.isUpperCase(passport.charAt(1)))
            throw new Exception("passport must be like XX 123456");
        this.fio = fio;
        this.passport = passport;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id_client=" + id_client +
                ", fio='" + fio + '\'' +
                ", passport='" + passport + '\'' +
                '}';
    }

    public int getId_client() {
        return id_client;
    }

    public void setId_client(int id_client) {
        this.id_client = id_client;
    }

    public String getFio() {
        return fio;
    }

    public String getPassport() {
        return passport;
    }
}
