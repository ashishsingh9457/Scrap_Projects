package HospitalManagementSystem;

import com.sun.security.jgss.GSSUtil;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url="jdbc:mysql://localhost:3306/hospital";

    private static final String username = "root";

    private static final String password = "ashish1234";

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        try {
            Connection connection = DriverManager.getConnection(url, username, password);

            Patient patient = new Patient(connection,scanner);
            Doctor doctor = new Doctor(connection);
            while (true)
            {
                System.out.println("HOSPITAL MANAGEMENT SYSTEM ");
                System.out.println("1. Add Patient");
                System.out.println("2. Add Doctor");
                System.out.println("3. View Patient");
                System.out.println("4. View Doctors");
                System.out.println("5. Book Appointment");
                System.out.println("6. View All Appointments");
                System.out.println("7. Delete an Appointment by id");
                System.out.println("8. Exit");

                System.out.println("Enter your choice ");

                int choice = scanner.nextInt();

                switch(choice)
                {
                    case 1:
                        patient.addPatient();
                        break;

                    case 2:
                        doctor.addDoctor();
                        break;

                    case 3:
                        patient.viewPatients();
                        break;

                    case 4:
                        doctor.viewDoctors();
                        break;

                    case 5:
                        bookAppointment(patient, doctor, connection, scanner);
                        break;

                    case 6:
                        viewAllAppointment(connection);
                        break;

                    case 7:
                        deleteAppointmentById(connection, scanner);
                        break;

                    case 8:
                        System.out.println("!!!  Thanks for using HOSPITAL MANAGEMENT SYSTEM  !!!");
                        return;

                    default:
                        System.out.println("Enter valid choice!!!");


                }

            }
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
    }
    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner)
    {
        System.out.println("Enter Patient Id: ");
        int patientId = scanner.nextInt();
        System.out.println("Enter Doctor Id: ");
        int doctorId = scanner.nextInt();
        System.out.println("Enter appointment date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();
        if(patient.getPatientById(patientId) && doctor.getDoctorById(doctorId))
        {
            if(checkDoctorAvailability(doctorId, appointmentDate, connection ))
            {
                String appointmentQuery = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES(?, ?, ?)";
                try{
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appointmentDate);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if(rowsAffected>0)
                    {
                        System.out.println("Appointment Booked!");
                    }
                    else
                    {
                        System.out.println("Failed to Book Appointment!");
                    }
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
            else{
                System.out.println("Either doctor or patient doesn't exist!!!");
            }
        }
        else{
            System.out.println("Either doctor or patient dosent't exist!!!");
        }
    }

    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection){
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next())
            {
                int count = resultSet.getInt(1);
                if(count==0)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public static void viewAllAppointment(Connection connection){
        String query = "SELECT * FROM appointments";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("Appointments: ");
            System.out.println("+----+-------------+-----------+------------");
            System.out.println("| Id | Patient_ID  | Doctor_ID  | Date      |");
            System.out.println("+----+-------------+-----------+------------");
            while(resultSet.next()){
                int id = resultSet.getInt("id");
                int patient_id = resultSet.getInt("patient_id");
                int doctor_Id = resultSet.getInt("doctor_id");
                String appointment_date = resultSet.getString("appointment_date");
                System.out.printf("| %-10s | %-18s | %-8s | %-10s |\n", id, patient_id, doctor_Id, appointment_date);
                System.out.println("+------------+----------------------+-------------+-----------");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static void deleteAppointmentById(Connection connection, Scanner scanner)
    {
        System.out.println("Enter Appointment Id: ");
        int appointmentId = scanner.nextInt();
        if(getAppointmentById(connection, appointmentId))
        {
            String deletionQuery = "DELETE FROM appointments WHERE id = ?";
            try{
                PreparedStatement preparedStatement = connection.prepareStatement(deletionQuery);
                preparedStatement.setInt(1, appointmentId);
                int rowsAffected = preparedStatement.executeUpdate();
                if(rowsAffected>0){
                    System.out.println("Appointment deleted!");
                }
                else {
                    System.out.println("Failed to Appointment Record!");
                }
            }
            catch (SQLException e){
                    e.printStackTrace();
            }
        }
        else{
            System.out.println("Appointment doesn't exist!!!");
        }
    }

    public static boolean getAppointmentById(Connection connection, int id)
    {
        String query = "SELECT * FROM appointments WHERE id = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return true;
            }
            else{
                return false;
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }


}
