import java.sql.Date;

public class Trip {
    private int tripId;
    private String source;
    private String destination;
    private String transportMode;
    private Date departureDate;
    private Date returnDate;
    private boolean hasAccommodation;
    private String status;

    public Trip(int tripId, String source, String destination, String transportMode, Date departureDate, Date returnDate, boolean hasAccommodation, String status) {
        this.tripId = tripId;
        this.source = source;
        this.destination = destination;
        this.transportMode = transportMode;
        this.departureDate = departureDate;
        this.returnDate = returnDate;
        this.hasAccommodation = hasAccommodation;
        this.status = status;
    }

    // Getters and setters (if needed)
    @Override
    public String toString() {
        return "Trip{" +
                "tripId=" + tripId +
                ", source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", transportMode='" + transportMode + '\'' +
                ", departureDate=" + departureDate +
                ", returnDate=" + returnDate +
                ", hasAccommodation=" + hasAccommodation +
                ", status='" + status + '\'' +
                '}';
    }
}
