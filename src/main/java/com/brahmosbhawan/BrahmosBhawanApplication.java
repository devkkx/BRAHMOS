package com.brahmosbhawan;

import com.brahmosbhawan.entity.ApprovedStudent;
import com.brahmosbhawan.entity.HostelBlock;
import com.brahmosbhawan.entity.Notice;
import com.brahmosbhawan.entity.Role;
import com.brahmosbhawan.entity.User;
import com.brahmosbhawan.repository.ApprovedStudentRepository;
import com.brahmosbhawan.repository.NoticeRepository;
import com.brahmosbhawan.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class BrahmosBhawanApplication {

    public static void main(String[] args) {
        SpringApplication.run(BrahmosBhawanApplication.class, args);
    }

    @Bean
    public CommandLineRunner seedDatabase(UserRepository userRepository, ApprovedStudentRepository approvedStudentRepository, NoticeRepository noticeRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Seed Admin User if not present
            if (!userRepository.existsByEmail("admin@brahmos.ac.in")) {
                User admin = new User(
                        "ADM001",
                        "Hostel Warden (Admin)",
                        "admin@brahmos.ac.in",
                        passwordEncoder.encode("admin123"),
                        "Admin-Office",
                        HostelBlock.A_BLOCK,
                        Role.ROLE_ADMIN
                );
                userRepository.save(admin);
                System.out.println(">>> Seeded default Admin user: admin@brahmos.ac.in / admin123");
            }

            // Seed Pre-Approved Boarder Whitelist
            if (!approvedStudentRepository.findByEmail("rahul@brahmos.ac.in").isPresent()) {
                ApprovedStudent app1 = new ApprovedStudent("ST101", "Rahul Kumar", "rahul@brahmos.ac.in", "101-A", HostelBlock.A_BLOCK);
                approvedStudentRepository.save(app1);
            }
            if (!approvedStudentRepository.findByEmail("priya@brahmos.ac.in").isPresent()) {
                ApprovedStudent app2 = new ApprovedStudent("ST102", "Priya Sharma", "priya@brahmos.ac.in", "204-C", HostelBlock.C_BLOCK);
                approvedStudentRepository.save(app2);
            }

            // Seed Demo Student 1 (A-Block)
            if (!userRepository.existsByEmail("rahul@brahmos.ac.in")) {
                User student1 = new User(
                        "ST101",
                        "Rahul Kumar",
                        "rahul@brahmos.ac.in",
                        passwordEncoder.encode("student123"),
                        "101-A",
                        HostelBlock.A_BLOCK,
                        Role.ROLE_STUDENT
                );
                userRepository.save(student1);
                System.out.println(">>> Seeded default Student user (A-Block): rahul@brahmos.ac.in / student123");
            }

            // Seed Demo Student 2 (C-Block)
            if (!userRepository.existsByEmail("priya@brahmos.ac.in")) {
                User student2 = new User(
                        "ST102",
                        "Priya Sharma",
                        "priya@brahmos.ac.in",
                        passwordEncoder.encode("student123"),
                        "204-C",
                        HostelBlock.C_BLOCK,
                        Role.ROLE_STUDENT
                );
                userRepository.save(student2);
                System.out.println(">>> Seeded default Student user (C-Block): priya@brahmos.ac.in / student123");
            }

            // Seed Demo Hostel Notices
            if (noticeRepository.count() == 0) {
                Notice n1 = new Notice(
                        "🚨 Mess Timing & 8-Hour Prior Cutoff Deadline Notice",
                        "All boarders of BRAHMOS BHAWAN (A-Block & C-Block) are hereby notified that daily Veg/Non-Veg mess preferences must be submitted or updated at least 8 hours prior to meal time (Lunch cutoff: 5:00 AM, Dinner cutoff: 12:00 PM). Single edit rule is enforced.",
                        "MESS",
                        "URGENT",
                        null,
                        "Chief Warden Office"
                );
                Notice n2 = new Notice(
                        "📢 Water Tank Cleaning Schedule (A-Block & C-Block)",
                        "Overhead water tanks in both A-Block and C-Block will undergo scheduled bi-weekly cleaning tomorrow between 9:00 AM and 12:00 PM. Please store required drinking water in advance.",
                        "MAINTENANCE",
                        "IMPORTANT",
                        null,
                        "Hostel Maintenance Manager"
                );
                noticeRepository.save(n1);
                noticeRepository.save(n2);
                System.out.println(">>> Seeded default Hostel Notices");
            }
        };
    }
}
