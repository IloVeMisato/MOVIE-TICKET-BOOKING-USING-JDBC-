package JDBC;

import java.sql.*;
import java.util.Scanner;

public class MovieTicketsBooking {

    private static final String url = "jdbc:mysql://localhost:3306/movieticketbooking";
    private static final String username = "root";
    private static final String password = "shadowball";


    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        try {
            Class.forName("com.mysql.jdbc.cj.Driver");
        }catch (ClassNotFoundException e ){
            System.out.println(e.getMessage());
        }

        try{
            Connection connection = DriverManager.getConnection(url,username,password);
            while (true){
                System.out.println();
                System.out.println(" - - - - - - WELCOME TO CINEPLIX - - - - - - ");
                Scanner scanner = new Scanner(System.in);
                System.out.println("VIEW TODAYS SHOWS: (Enter number 5: )");
                System.out.println("1. Book a ticket for a movie you want to watch -- -- -- ");
                System.out.println("2. View Movie Ticket");
                System.out.println("3. Update the Ticket");
                System.out.println("4.Delete Ticket Reservations");
                System.out.println();
                System.out.println("Choose an option according to your preference: ");

                int yourChoice = scanner.nextInt();

                switch (yourChoice){
                    case 1:
                        reserveTicket(connection,scanner);
                        break;
                    case 2:
                        viewMyTicket(connection);
                        break;
                    case 3:
                        updateShows(connection,scanner);
                        break;
                    case 4:
                        deleteBooking(connection,scanner);
                        break;
                    case 5:
                        movieShowsForToday(connection,scanner);
                        break;
                    default:
                        System.out.println("Invalid choice, Please try again later.");
                }
            }

        }catch(SQLException e ){
            System.out.println(e.getMessage());;

        }catch (Exception e ){ // wait till you enter the above methods. // Interrupted exception
           throw  new RuntimeException(e);
        }
    }

    public  static  void reserveTicket(Connection connection,Scanner scanner){
        try {
            System.out.println("Enter your Name: ");
            String yourName = scanner.nextLine();
            System.out.println("Enter your Seat Number : ");
            String seatNumber = scanner.nextLine();
            System.out.println("Enter your contact number - ");
            String contactNumber = scanner.next();

            String sql = "INSERT INTO reserveticket(name,seat_number,contact_number) " +
                    "VALUES(' " + yourName +    " ','" + seatNumber  + "','" +   contactNumber + "')";

            try (Statement statement = connection.createStatement()){
                int rowsAffected = statement.executeUpdate(sql);
                if (rowsAffected>0){
                    System.out.println("Reservation success --------\n" +
                            "ENJOY YOUR MOVIE");
                }else {
                    System.out.println("No Success found");
                }
            }


        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void movieShowsForToday(Connection connection,Scanner scanner){
        System.out.println("Movies for shows in today");
        System.out.println();
        System.out.println("Race 3 --SHOW TIMINGS ARE 10 A.M to 1 P.M ");
        System.out.println();
        System.out.println("3 Idiots --SHOW TIMINGS ARE 1 P.M to 5 P.M");
        System.out.println();
        System.out.println("Natsamrat --SHOW TIMINGS ARE 5 P.M to 8 P.M");
        System.out.println();
        System.out.println("MUMBAI PUNE MUMBAI --SHOW TIMINGS ARE 9 P.M to 12 A.M");


    }

    private static void viewMyTicket(Connection connection) throws  SQLException{
        String sql = "SELECT reservation_id,name,seat_number,contact_number,reservation_date,moviename FROM reserveticket;";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)){
            System.out.println("Current Reservation");

            System.out.println("||||--------------------------------------------+-----------------------------------------------------|||||");
            System.out.println("| RESERVATION ID    | NAME    | SEAT NUMBER    | CONTACT NUMBER    | DATE AND TIME    | MOVIE NAME    | ");
            System.out.println("||||--------------------------------------------+-----------------------------------------------------|||||");

            while (resultSet.next()){
                int reservationID = resultSet.getInt("reservation_id");
                String name = resultSet.getString("name");
                int seatNumber = resultSet.getInt("seat_number");
                String contactNumber = resultSet.getString("contact_number");
                String reservationDate = resultSet.getTimestamp("reservation_date").toString();
                String movieName  = resultSet.getString("moviename");

                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        reservationID , name, seatNumber, contactNumber, reservationDate, movieName);
            }
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
        }
    }

    public static void updateShows(Connection connection,Scanner scanner){
        try{
            System.out.println("Enter Reservation ID to Update: ");
            int reservationId = scanner.nextInt();
            scanner.nextLine();

            if(!reservationExists(connection,reservationId)){
                System.out.println("NOT FOUND, TRY AGAIN LATER");
                return;
            }

            System.out.println("Enter New Entry: ");
            String newEntry = scanner.nextLine();
            System.out.println("Enter New Seat Number");
            int newSeat = scanner.nextInt();
            System.out.println("Enter New Contact Number");
            String newContact = scanner.next();

            String sql = "UPDATE reserveticket SET name = '" + newEntry + "', " +
                    "seat_number = " + newSeat + ", " +
                    "contact_number = '" + newContact + "' " +
                    "WHERE reservation_id = " + reservationId;;

            try(Statement statement = connection.createStatement()) {
                int rowsAffected = statement.executeUpdate(sql);

                if (rowsAffected>0){
                    System.out.println("Reservation updated successfully!");
                }else {
                    System.out.println("FAILED --- TRY AGAIN ");
                }
            }


        }catch (SQLException e ){
            e.printStackTrace();
        }
    }

    public static void deleteBooking(Connection connection, Scanner scanner){
        try{
            System.out.println("ENTER THE RESERVATION ID TO DELETE : ");
            int reservationId = scanner.nextInt();

            if (!reservationExists(connection,reservationId)){
                System.out.println("NOT FOUND");
                return;
            }

            String sql = "DELETE FROM reserveticket WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement()){
                int affectedRows = statement.executeUpdate(sql);

                if(affectedRows>0){
                    System.out.println("Reservation deleted Successfully");
                }else {
                    System.out.println("Reservation deletion failed");
                }

            }

        }catch (SQLException e ){
            e.printStackTrace();
        }

    }

    public static boolean reservationExists(Connection connection, int reservationId){ // boolean cause true or false.
        try {
            String sql = "SELECT reservation_id FROM reserveticket WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                return resultSet.next(); // If there's a result, the reservation exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Handle database errors as needed
        }
    }


// something wrong ,





}
