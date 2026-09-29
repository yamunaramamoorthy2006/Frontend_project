import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MovieTicketBooking {

    static Scanner sc = new Scanner(System.in);

    // ================= DATA =================

    static List<String> movies = new ArrayList<>(
            Arrays.asList("Theri", "Vikram", "Jailer")
    );

    static List<String> availableSeats = new ArrayList<>(
            Arrays.asList(
                    "A1", "A2", "A3", "A4", "A5",
                    "B1", "B2", "B3", "B4", "B5",
                    "C1", "C2", "C3", "C4", "C5"
            )
    );

    static List<Booking> bookings = new ArrayList<>();

    static int nextBookingId = 1001;

    static final double TICKET_PRICE = 189.0;


    // ================= BOOKING CLASS =================

    static class Booking {

        int bookingId;
        String customerName;
        String movie;
        List<String> seats;
        double amount;

        Booking(
                int bookingId,
                String customerName,
                String movie,
                List<String> seats,
                double amount
        ) {

            this.bookingId = bookingId;
            this.customerName = customerName;
            this.movie = movie;
            this.seats = seats;
            this.amount = amount;
        }

        public int getBookingId() {
            return bookingId;
        }

        public String getCustomerName() {
            return customerName;
        }

        public String getMovie() {
            return movie;
        }

        public List<String> getSeats() {
            return seats;
        }

        public double getAmount() {
            return amount;
        }

        void display() {

            System.out.println("\n--------------------------------");
            System.out.println("Booking ID : " + bookingId);
            System.out.println("Customer   : " + customerName);
            System.out.println("Movie      : " + movie);
            System.out.println("Seats      : " + seats);
            System.out.println("Amount     : ₹" + amount);
            System.out.println("--------------------------------");
        }
    }


    // ================= MAIN =================

    public static void main(String[] args) {

        while (true) {

            System.out.println("\n========== MOVIE TICKET BOOKING ==========");

            System.out.println("1. Customer");
            System.out.println("2. Admin");
            System.out.println("3. Exit");

            System.out.print("Enter choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    customerMenu();
                    break;

                case 2:
                    adminMenu();
                    break;

                case 3:
                    System.out.println("Thank you!");
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }


    // ================= CUSTOMER MENU =================

    static void customerMenu() {

        while (true) {

            System.out.println("\n========== CUSTOMER MENU ==========");

            System.out.println("1. Book Ticket");
            System.out.println("2. View Booking History");
            System.out.println("3. Cancel Booking");
            System.out.println("4. Back");

            System.out.print("Enter choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    bookTicket();
                    break;

                case 2:
                    viewBookingHistory();
                    break;

                case 3:
                    cancelBooking();
                    break;

                case 4:
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }


    // ================= BOOK TICKET =================

    static void bookTicket() {

        System.out.println("\n========== BOOK TICKET ==========");

        // Customer name
        System.out.print("Enter customer name: ");
        String customerName = sc.nextLine();


        // Movie selection
        System.out.println("\nAvailable Movies:");

        for (int i = 0; i < movies.size(); i++) {

            System.out.println(
                    (i + 1) + ". " + movies.get(i)
            );
        }

        System.out.print("Select movie: ");

        int movieChoice = sc.nextInt();
        sc.nextLine();

        if (movieChoice < 1 || movieChoice > movies.size()) {

            System.out.println("Invalid movie!");
            return;
        }

        String movie = movies.get(movieChoice - 1);


        // Seat display
        System.out.println("\n========== AVAILABLE SEATS ==========");

        System.out.println(availableSeats);

        System.out.print("How many seats do you want? ");

        int seatCount = sc.nextInt();
        sc.nextLine();

        if (seatCount <= 0) {

            System.out.println("Invalid seat count!");
            return;
        }

        if (seatCount > availableSeats.size()) {

            System.out.println("Not enough seats available!");
            return;
        }


        // Seat selection
        List<String> selectedSeats = new ArrayList<>();

        for (int i = 0; i < seatCount; i++) {

            System.out.print(
                    "Enter seat " + (i + 1) + ": "
            );

            String seat = sc.nextLine().toUpperCase();

            if (!availableSeats.contains(seat)) {

                System.out.println(
                        "Seat " + seat + " is not available!"
                );

                return;
            }

            if (selectedSeats.contains(seat)) {

                System.out.println(
                        "You already selected " + seat
                );

                return;
            }

            selectedSeats.add(seat);
        }


        // Calculate amount
        double amount = seatCount * TICKET_PRICE;

        int bookingId = nextBookingId++;

        Booking booking = new Booking(
                bookingId,
                customerName,
                movie,
                selectedSeats,
                amount
        );


        // =================================================
        // COMPLETABLE FUTURE
        // =================================================

        CompletableFuture<Booking> bookingFuture =
                CompletableFuture.supplyAsync(() -> {

                    System.out.println(
                            "\n? Processing ticket confirmation..."
                    );

                    try {

                        Thread.sleep(2000);

                    } catch (InterruptedException e) {

                        Thread.currentThread().interrupt();
                    }

                    return booking;
                });


        CompletableFuture<Booking> confirmedBooking =
                bookingFuture.thenApply(result -> {

                    // Remove seats only after confirmation

                    availableSeats.removeAll(
                            result.getSeats()
                    );

                    bookings.add(result);

                    System.out.println(
                            "? Ticket confirmation completed!"
                    );

                    return result;
                });


        CompletableFuture<Void> ticketReady =
                confirmedBooking.thenAccept(result -> {

                    System.out.println("\n? TICKET READY!");

                    System.out.println(
                            "Booking ID : "
                                    + result.getBookingId()
                    );

                    System.out.println(
                            "Customer   : "
                                    + result.getCustomerName()
                    );

                    System.out.println(
                            "Movie      : "
                                    + result.getMovie()
                    );

                    System.out.println(
                            "Seats      : "
                                    + result.getSeats()
                    );

                    System.out.println(
                            "Amount     : ₹"
                                    + result.getAmount()
                    );
                });


        // =================================================
        // IMPORTANT FIX
        // =================================================

        // Wait until ticket confirmation is COMPLETED.
        // Only AFTER that Customer Menu will appear.

        ticketReady.join();


        System.out.println(
                "\nReturning to Customer Menu..."
        );
    }


    // ================= BOOKING HISTORY =================

    static void viewBookingHistory() {

        System.out.println(
                "\n========== BOOKING HISTORY =========="
        );

        if (bookings.isEmpty()) {

            System.out.println(
                    "No bookings found."
            );

            return;
        }

        for (Booking booking : bookings) {

            booking.display();
        }
    }


    // ================= CANCEL BOOKING =================

    static void cancelBooking() {

        System.out.println(
                "\n========== CANCEL BOOKING =========="
        );

        if (bookings.isEmpty()) {

            System.out.println(
                    "No bookings available."
            );

            return;
        }

        System.out.print(
                "Enter Booking ID: "
        );

        int bookingId = sc.nextInt();
        sc.nextLine();


        Booking selectedBooking = null;

        for (Booking booking : bookings) {

            if (booking.getBookingId() == bookingId) {

                selectedBooking = booking;
                break;
            }
        }


        if (selectedBooking == null) {

            System.out.println(
                    "Booking not found!"
            );

            return;
        }


        Booking finalBooking = selectedBooking;


        CompletableFuture<Void> cancelFuture =
                CompletableFuture.runAsync(() -> {

                    System.out.println(
                            "\n? Processing cancellation..."
                    );

                    try {

                        Thread.sleep(1500);

                    } catch (InterruptedException e) {

                        Thread.currentThread().interrupt();
                    }

                    availableSeats.addAll(
                            finalBooking.getSeats()
                    );

                    bookings.remove(finalBooking);

                    System.out.println(
                            "? Booking cancelled successfully!"
                    );

                    System.out.println(
                            "Refund Amount : ₹"
                                    + finalBooking.getAmount()
                    );
                });


        // Wait for cancellation
        cancelFuture.join();
    }


    // ================= ADMIN MENU =================

    static void adminMenu() {

        while (true) {

            System.out.println(
                    "\n========== ADMIN MENU =========="
            );

            System.out.println("1. Add Movie");
            System.out.println("2. Remove Movie");
            System.out.println("3. View Movies");
            System.out.println("4. View Bookings");
            System.out.println("5. Back");

            System.out.print("Enter choice: ");

            int choice = sc.nextInt();
            sc.nextLine();


            switch (choice) {

                case 1:
                    addMovie();
                    break;

                case 2:
                    removeMovie();
                    break;

                case 3:
                    viewMovies();
                    break;

                case 4:
                    viewBookingHistory();
                    break;

                case 5:
                    return;

                default:
                    System.out.println(
                            "Invalid choice!"
                    );
            }
        }
    }


    // ================= ADD MOVIE =================

    static void addMovie() {

        System.out.print(
                "Enter movie name: "
        );

        String movie = sc.nextLine();

        movies.add(movie);

        System.out.println(
                "? Movie added successfully!"
        );
    }


    // ================= REMOVE MOVIE =================

    static void removeMovie() {

        viewMovies();

        System.out.print(
                "Enter movie name to remove: "
        );

        String movie = sc.nextLine();

        if (movies.remove(movie)) {

            System.out.println(
                    "? Movie removed successfully!"
            );

        } else {

            System.out.println(
                    "Movie not found!"
            );
        }
    }


    // ================= VIEW MOVIES =================

    static void viewMovies() {

        System.out.println(
                "\n========== MOVIES =========="
        );

        if (movies.isEmpty()) {

            System.out.println(
                    "No movies available."
            );

            return;
        }

        for (int i = 0; i < movies.size(); i++) {

            System.out.println(
                    (i + 1) + ". " + movies.get(i)
            );
        }
    }
}