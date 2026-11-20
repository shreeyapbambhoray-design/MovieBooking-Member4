
import java.util.ArrayList;
import java.util.List;

public class BookingManager {

    private List<Booking> bookings;
    private SeatManager seatManager;
    private int nextBookingId;

    public BookingManager(SeatManager seatManager) {
        this.bookings = new ArrayList<>();
        this.seatManager = seatManager;
        this.nextBookingId = 1001;
    }

    // Calculate total ticket price
    public double calculateBill(Movie movie, List<String> seats) {
        return movie.getTicketPrice() * seats.size();
    }

    // Create a new booking
    public Booking createBooking(Customer customer, Movie movie,
                                 String showtime, List<String> seats) {

        String showKey = movie.getMovieId() + "_" + showtime;

        if (!seatManager.areAvailable(showKey, seats)) {
            System.out.println("Booking failed! Selected seats are unavailable.");
            return null;
        }

        double totalAmount = calculateBill(movie, seats);

        seatManager.reserveSeats(showKey, seats);

        Booking booking = new Booking(
                nextBookingId++,
                customer,
                movie,
                showtime,
                seats,
                totalAmount
        );

        bookings.add(booking);

        // Display booking confirmation receipt
        displayBookingReceipt(booking, customer, movie, showtime, seats);

        return booking;
    }

    // Display detailed booking confirmation receipt
    private void displayBookingReceipt(Booking booking,
                                       Customer customer,
                                       Movie movie,
                                       String showtime,
                                       List<String> seats) {

        System.out.println("\n==================================");
        System.out.println("       BOOKING CONFIRMATION");
        System.out.println("==================================");

        System.out.println("Booking ID    : " + booking.getBookingId());
        System.out.println("Customer Name : " + customer.getName());
        System.out.println("Movie Name    : " + movie.getMovieName());
        System.out.println("Showtime      : " + showtime);
        System.out.println("Selected Seats: " + String.join(", ", seats));
        System.out.println("Number of Seats: " + seats.size());

        System.out.printf("Ticket Price  : Rs. %.2f%n",
                movie.getTicketPrice());

        System.out.printf("Total Amount  : Rs. %.2f%n",
                booking.getTotalAmount());

        System.out.println("----------------------------------");
        System.out.println("       BOOKING SUCCESSFUL!");
        System.out.println("==================================");
    }

    // View booking history from newest to oldest
    public void viewBookings() {

        if (bookings.isEmpty()) {
            System.out.println("\nNo bookings found.");
            return;
        }

        System.out.println("\n===== BOOKING HISTORY =====");
        System.out.println("Bookings displayed from newest to oldest.");

        for (int i = bookings.size() - 1; i >= 0; i--) {
            Booking booking = bookings.get(i);
            booking.displayBooking();
        }
    }

    // Search booking using Booking ID
    public void searchBooking(int bookingId) {

        for (Booking booking : bookings) {

            if (booking.getBookingId() == bookingId) {
                System.out.println("\n===== BOOKING FOUND =====");
                booking.displayBooking();
                return;
            }
        }

        System.out.println("No booking found with ID: " + bookingId);
    }

    // Cancel booking and release seats
   // Cancel booking with proper validation
public void cancelBooking(int bookingId) {

    if (bookingId <= 0) {
        System.out.println("Invalid booking ID!");
        return;
    }

    for (Booking booking : bookings) {

        if (booking.getBookingId() == bookingId) {

            if (booking.isCancelled()) {
                System.out.println("This booking is already cancelled!");
                return;
            }

            String showKey = booking.getMovie().getMovieId()
                    + "_" + booking.getShowtime();

            seatManager.releaseSeats(
                    showKey,
                    booking.getSelectedSeats()
            );

            booking.cancelBooking();

            System.out.println("Booking cancelled successfully!");
            return;
        }
    }

    System.out.println("Booking ID not found!");
}

    // Display booking history summary and revenue
    public void displayBookingSummary() {

        int totalBookings = bookings.size();
        int confirmedBookings = 0;
        int cancelledBookings = 0;
        double totalRevenue = 0;

        for (Booking booking : bookings) {

            if (booking.isCancelled()) {
                cancelledBookings++;
            } else {
                confirmedBookings++;
                totalRevenue += booking.getTotalAmount();
            }
        }

        System.out.println("\n===== BOOKING SUMMARY =====");
        System.out.println("Total Bookings: " + totalBookings);
        System.out.println("Confirmed Bookings: " + confirmedBookings);
        System.out.println("Cancelled Bookings: " + cancelledBookings);
        System.out.printf("Total Revenue: Rs. %.2f%n", totalRevenue);
    }
}
