//package org.example.thuan_security;
//
//
//import org.example.thuan_security.model.Roles;
//import org.example.thuan_security.repository.RoleRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//public class RoleInitializer implements CommandLineRunner {
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    @Override
//    public void run(String... args) {
//        // Kiểm tra xem role ROLE_USER đã tồn tại chưa
//        if (roleRepository.findByName("ROLE_USER") == null) {
//            // Nếu chưa, thêm role vào cơ sở dữ liệu
//            Roles roleUser = new Roles();
//            roleUser.setName("ROLE_USER");
//            roleRepository.save(roleUser);
//
//            System.out.println("Added default role: ROLE_USER");
//        } else {
//            System.out.println("ROLE_USER already exists");
//        }
//    }
//}
//
