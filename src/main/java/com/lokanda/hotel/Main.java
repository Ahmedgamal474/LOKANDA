package com.lokanda.hotel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.lokanda.hotel.model.booking.Booking;
import com.lokanda.hotel.model.booking.BookingItem;
import com.lokanda.hotel.model.bookingpackage.BreakfastOnly;
import com.lokanda.hotel.model.offer.Offer;
import com.lokanda.hotel.model.payment.Payment;
import com.lokanda.hotel.model.payment.PaymentMethod;
import com.lokanda.hotel.model.feedback.Feedback;
import com.lokanda.hotel.model.bookingpackage.HalfBoard;
import com.lokanda.hotel.model.bookingpackage.AllInclusive;
import com.lokanda.hotel.model.person.Guest;
import com.lokanda.hotel.model.person.Receptionist;
import com.lokanda.hotel.model.room.Room;
import com.lokanda.hotel.model.room.RoomType;
import com.lokanda.hotel.service.ReservationService;

public class Main {

    public static void main(String[] args) {

        System.out.println("======================================");
        System.out.println("       LOKANDA HOTEL SYSTEM");
        System.out.println("======================================");

        try {
            // Create the hotel's rooms.
            Room room1 = new Room(1, "101", RoomType.SINGLE);
            Room room2 = new Room(2, "102", RoomType.DOUBLE);
            Room room3 = new Room(3, "201", RoomType.SUITE);

            List<Room> hotelRooms = new ArrayList<>();
            hotelRooms.add(room1);
            hotelRooms.add(room2);
            hotelRooms.add(room3);

            // Create the reservation service.
            ReservationService reservationService =
                    new ReservationService(hotelRooms);

            LocalDate checkIn = LocalDate.now().plusDays(10);
            LocalDate checkOut = checkIn.plusDays(3);

            System.out.println("\n1. Checking available single rooms...");
            List<Room> availableRooms = reservationService.getAvailableRooms(
                    checkIn, checkOut, RoomType.SINGLE);

            for (Room room : availableRooms) {
                System.out.println("Available room: " + room.getRoomNumber());
            }

            // Create the guest and receptionist.
            Guest guest = new Guest(
                    1,
                    "Hamza",
                    "Mahdi",
                    "hamza@example.com",
                    "01012345678",
                    "30001011234567"
            );

            Receptionist receptionist = new Receptionist(
                    1,
                    "Ahmed",
                    "Ali",
                    "ahmed@example.com",
                    "01112345678",
                    "EMP-001"
            );

            // Create the booking package and calculate its price.
            BreakfastOnly breakfastPackage = new BreakfastOnly();

            BigDecimal packagePrice = breakfastPackage.calculatePackagePrice(
                    room1.getType(), checkIn, checkOut);

            BookingItem item = new BookingItem(
                    1, room1, breakfastPackage, packagePrice);

            // Create the booking.
            Booking booking = new Booking(
                    guest, checkIn, checkOut, receptionist);

            booking.setBookingId(1001);
            booking.addItem(item);

            // Create and apply an offer.
            Offer offer = new Offer(
                    1,
                    "Welcome Offer",
                    "10 percent discount for a new reservation",
                    10,
                    checkIn.minusDays(1),
                    checkOut.plusDays(1),
                    true
            );

            booking.setOffer(offer);

            System.out.println("\n2. Creating reservation...");
            reservationService.createReservation(booking);

            System.out.println("Reservation created successfully.");
            System.out.println("Booking ID: " + booking.getBookingId());
            System.out.println("Guest: " + guest.getFullName());
            System.out.println("Room: " + room1.getRoomNumber());
            System.out.println("Package: " + breakfastPackage.getName());
            System.out.println("Total before discount: "
                    + booking.calculateTotal());
            System.out.println("Discount: " + booking.getDiscountAmount());
            System.out.println("Final price: "
                    + booking.calculateFinalPrice());
            System.out.println("Status: " + booking.getStatus());

            System.out.println("\n3. Checking room availability after booking...");
            availableRooms = reservationService.getAvailableRooms(
                    checkIn, checkOut, RoomType.SINGLE);

            System.out.println("Available single rooms: "
                    + availableRooms.size());

            System.out.println("\n4. Finding reservation...");
            Booking foundBooking =
                    reservationService.findReservation(1001);

            System.out.println("Found booking: "
                    + foundBooking.getBookingId());

            System.out.println("\n5. Modifying reservation dates...");
            reservationService.modifyReservation(
                    1001, checkIn.plusDays(1), checkOut.plusDays(1));

            System.out.println("Reservation dates modified successfully.");

            System.out.println("\n6. Cancelling reservation...");
            reservationService.cancelReservation(1001);

            System.out.println("Reservation cancelled successfully.");
            System.out.println("Final status: " + booking.getStatus());

            System.out.println("\n7. Testing the other hotel packages...");
            System.out.println("Half Board price: "
                    + new HalfBoard().calculatePackagePrice(
                            RoomType.DOUBLE, checkIn, checkOut));
            System.out.println("All Inclusive price: "
                    + new AllInclusive().calculatePackagePrice(
                            RoomType.SUITE, checkIn, checkOut));

            System.out.println("\n8. Testing payment...");
            Payment payment = new Payment(
                    booking.calculateFinalPrice(), PaymentMethod.CASH);
            System.out.println("Payment processed: "
                    + payment.processPayment());
            System.out.println("Payment status: " + payment.getStatus());

            System.out.println("\n9. Testing feedback...");
            Feedback feedback = new Feedback(5, "Excellent stay");
            System.out.println("Feedback: " + feedback);

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("\nOperation failed: " + e.getMessage());
        }

        System.out.println("\n======================================");
        System.out.println("       LOKANDA SYSTEM FINISHED");
        System.out.println("======================================");
    }
}
