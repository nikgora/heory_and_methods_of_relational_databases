public class Room {
    int number_room;
    int capacity;
    int ref_comfort;
    float price;

    public Room(int number_room, int capacity, String comfort, float price) throws Exception {
        if (capacity > 4 || capacity < 1) throw new Exception("capacity must be between 1 and 4");
        if (price < 20) throw new Exception("price must be more then 19");

        this.number_room = number_room;
        this.capacity = capacity;
        this.ref_comfort = Accessor.getInstance().getIdComfort(comfort);
        this.price = price;
    }

    public int getNumber_room() {
        return number_room;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getRef_comfort() {
        return ref_comfort;
    }

    public float getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Room{" +
                "number_room=" + number_room +
                ", capacity=" + capacity +
                ", ref_comfort=" + ref_comfort +
                ", price=" + price +
                '}';
    }
}
