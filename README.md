# Ecom Store

Website bán hàng trực tuyến được phát triển bằng **Java Spring Boot**. Hệ thống hỗ trợ người dùng xem và tìm kiếm sản phẩm, thêm vào giỏ hàng, đặt hàng và thanh toán trực tuyến qua mã QR (VNPay). Hệ thống Quản trị (Admin) cho phép quản lý danh mục, sản phẩm, đơn hàng và tài khoản người dùng.

* ** Demo:** [https://e-commerce-store-q62b.onrender.com](https://e-commerce-store-q62b.onrender.com)  
  *(Lưu ý: Ứng dụng chạy trên gói miễn phí của Render nên ở lần truy cập đầu tiên có thể mất khoảng 1 phút để máy chủ khởi động lại.)*

---

## Chức năng chính

### Người dùng (User)
* **Tài khoản & Xác thực:** Đăng ký, đăng nhập bằng email hoặc tài khoản Google (OAuth2).
* **Bảo mật & Xác thực email:** Tích hợp OTP qua email; hỗ trợ quên mật khẩu và đặt lại mật khẩu bằng liên kết an toàn qua email.
* **Mua sắm & Trải nghiệm:** Xem danh sách sản phẩm, lọc theo danh mục, tìm kiếm theo tên và phân trang mượt mà.
* **Giỏ hàng & Đặt hàng:** Quản lý giỏ hàng, tiến hành đặt hàng và xem lịch sử đơn hàng chi tiết.
* **Thanh toán trực tuyến:** Thanh toán quét mã QR tự động qua cổng **VNPay**.
* **Thông báo:** Tự động nhận email xác nhận khi đặt hàng thành công và khi trạng thái đơn hàng thay đổi.

### Quản trị (Admin)
* **Quản lý danh mục:** Thêm, sửa, xóa danh mục sản phẩm.
* **Quản lý sản phẩm:** Thêm, sửa, xóa mềm (Soft Delete), tìm kiếm và phân trang sản phẩm.
* **Quản lý đơn hàng:** Xem danh sách đơn hàng, tìm kiếm theo mã đơn và cập nhật trạng thái đơn hàng.
* **Quản lý người dùng:** Xem danh sách và thực hiện khóa / mở khóa tài khoản người dùng.
* **Phân quyền hệ thống:** Phân quyền nghiêm ngặt giữa `Admin` và `User` sử dụng Spring Security.

> **Giao diện:** Thiết kế tương thích chuẩn Responsive trên điện thoại và thiết bị di động bằng Bootstrap 5.

---

##  Công nghệ sử dụng

* **Ngôn ngữ:** Java 21[cite: 3]
* **Backend:** Spring Boot 3.2, Spring MVC, Spring Security 6, Spring Data JPA (Hibernate)[cite: 3]
* **Giao diện:** Thymeleaf, Bootstrap 5, HTML, CSS, JavaScript[cite: 3]
* **Cơ sở dữ liệu:** MySQL[cite: 3]
* **Thanh toán:** VNPay Gateway API[cite: 3]
* **Công cụ:** Maven, Git, Docker, Eclipse IDE / VS Code[cite: 3]
* **Triển khai:** Render Cloud Platform[cite: 3]

## Cấu trúc thư mục dự án

```text
src/main/java/com/example/Shopping_Cart
├── Controller     # Nhận HTTP request, điều hướng và trả về giao diện
├── service        # Khai báo các interface xử lý nghiệp vụ
├── service/impl   # Hiện thực hóa các logic nghiệp vụ
├── repository     # Truy vấn cơ sở dữ liệu (Spring Data JPA)
├── model          # Các Entity và Data Transfer Objects (DTO)
├── config         # Cấu hình Spring Security, OAuth2, VNPay...
└── util           # Các hàm tiện ích (gửi Email, tạo OTP,...)

src/main/resources
├── templates      # Trang giao diện Thymeleaf (admin, user,...)
└── static         # Chứa tài nguyên tĩnh (CSS, JS, Hình ảnh)

## Hướng dẫn chạy trên máy cá nhân (Local)

**Yêu cầu môi trường:** Java 21 trở lên, Maven 3.x, MySQL 8.0+.

1. **Tải mã nguồn về máy:**
   ```bash
   git clone [https://github.com/caubevuive/e-commerce-store.git](https://github.com/caubevuive/e-commerce-store.git)
   cd e-commerce-store
Tạo cơ sở dữ liệu trống trong MySQL:

SQL
CREATE DATABASE ecommerce_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
Tạo file cấu hình cá nhân:

Bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
Điền thông tin cấu hình:
Mở file application.properties vừa tạo và điền đầy đủ các thông tin: kết nối MySQL, tài khoản email gửi thư (SMTP App Password), Google OAuth2 Client ID/Secret và VNPay Secret Key.

(Lưu ý: Không đưa file application.properties chứa mật khẩu thật lên GitHub).

Biên dịch và chạy ứng dụng:
Bash
mvn spring-boot:run
Truy cập ứng dụng:
Mở trình duyệt web và truy cập địa chỉ: http://localhost:8080
Khởi tạo tài khoản Admin đầu tiên: Các bảng cơ sở dữ liệu sẽ được Hibernate tự động tạo khi chạy lần đầu. Sau khi đăng ký một tài khoản thường trên giao diện web, truy cập MySQL Workbench/DBeaver và sửa giá trị cột role trong bảng user_dtls thành ROLE_ADMIN.

Hướng dẫn chạy bằng Docker
Build Docker Image:

Bash
docker build -t ecom-store .
Chạy Docker Container:

Bash
docker run -p 8080:8080 --env-file .env ecom-store
(Tạo một file .env ở thư mục gốc chứa các biến môi trường như mẫu bên dưới):

Đoạn mã
SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/ecommerce_db
SPRING_DATASOURCE_USERNAME=
SPRING_DATASOURCE_PASSWORD=your_mysql_password
SPRING_MAIL_USERNAME=your_email@gmail.com
SPRING_MAIL_PASSWORD=your_app_password


Hạn chế hiện tại & Hướng phát triển
Lưu trữ hình ảnh: Ảnh sản phẩm hiện đang được lưu trực tiếp trong thư mục ứng dụng nên có thể bị xóa khi ứng dụng redeploy trên Cloud. Dự kiến: Chuyển sang lưu trữ đám mây AWS S3.

Dịch vụ Email: Gói miễn phí của Render chặn cổng gửi email SMTP truyền thống. Dự kiến: Chuyển sang sử dụng API HTTP (Brevo API hoặc AWS SES).

Hạ tầng triển khai: Dự kiến chuyển toàn bộ hạ tầng sang AWS (EC2 cho server, RDS cho MySQL, S3 cho tệp tĩnh).

👨‍💻 Tác giả
Vũ Anh Quân — Java Backend Developer Intern
Email: anhquanvu44@gmail.com
GitHub: github.com/caubevuive
