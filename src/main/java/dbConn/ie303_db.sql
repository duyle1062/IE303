CREATE TABLE CUSTOMER (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    phone VARCHAR(15),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ADMIN (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    phone VARCHAR(15),
    address TEXT,
    hire_date DATE NOT NULL,
    department VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE AUTHOR (
    author_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    birth_date DATE,
    nationality VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE BOOKS (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    isbn VARCHAR(13) NOT NULL UNIQUE,
    description TEXT,
    publication_year INT,
    copies_available INT NOT NULL,
    author_id INT,
    FOREIGN KEY (author_id) REFERENCES AUTHOR(author_id)
);

CREATE TABLE GENRE (
    genre_id INT AUTO_INCREMENT PRIMARY KEY,
    genre_name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE BOOK_GENRE (
    book_genre_id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT,
    genre_id INT,
    FOREIGN KEY (book_id) REFERENCES BOOKS(book_id),
    FOREIGN KEY (genre_id) REFERENCES GENRE(genre_id)
);

CREATE TABLE RESERVATIONS (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    book_id INT,
    reservation_date DATE,
    status ENUM('active', 'cancelled', 'fulfilled') DEFAULT 'active',
    FOREIGN KEY (customer_id) REFERENCES CUSTOMER(customer_id),
    FOREIGN KEY (book_id) REFERENCES BOOKS(book_id)
);


CREATE TABLE BORROWING (
    borrowing_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    admin_id INT,
    bill_id INT,
    borrow_date DATE,
    due_date DATE,
    return_date DATE,
    status ENUM('borrowed', 'returned', 'overdue') DEFAULT 'borrowed',
    borrowing_fee DECIMAL(10,2) DEFAULT 0.00,
    overdue_fee DECIMAL(10,2) DEFAULT 0.00,
    FOREIGN KEY (customer_id) REFERENCES CUSTOMER(customer_id),
    FOREIGN KEY (admin_id) REFERENCES ADMIN(admin_id)
);


CREATE TABLE BILL (
    bill_id INT AUTO_INCREMENT PRIMARY KEY,
    borrowing_id int,
    total_amount DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (borrowing_id) REFERENCES BORROWING(borrowing_id)
);

CREATE TABLE BOOK_BORROW (
    book_borrow_id INT AUTO_INCREMENT PRIMARY KEY,
    borrowing_id INT,
    book_id INT,
    quantity INT NOT NULL,
    FOREIGN KEY (borrowing_id) REFERENCES BORROWING(borrowing_id),
    FOREIGN KEY (book_id) REFERENCES BOOKS(book_id)
);

INSERT INTO CUSTOMER (username, password, email, first_name, last_name, phone, address)
VALUES ('nguyenvana', 'matkhau123', 'nguyenvana@example.com', 'Văn A', 'Nguyễn', '0901234567', '123 Đường Lê Lợi, Quận 1, TP. Hồ Chí Minh');
INSERT INTO CUSTOMER (username, password, email, first_name, last_name, phone, address)
VALUES ('tranthib', '123abcxyz', 'tranthib@example.com', 'Thị B', 'Trần', '0912345678', '456 Phan Xích Long, Phú Nhuận, TP. Hồ Chí Minh');
INSERT INTO CUSTOMER (username, password, email, first_name, last_name, phone, address)
VALUES ('lequangh', 'mk_secure_2025', 'lequangh@example.com', 'Quang H', 'Lê', '0923456789', '789 Hoàng Văn Thụ, Tân Bình, TP. Hồ Chí Minh');
INSERT INTO CUSTOMER (username, password, email, first_name, last_name, phone, address)
VALUES ('phamminhthanh', 'pass2025', 'phamminhthanh@example.com', 'Minh Thành', 'Phạm', '0934567890', '12 Nguyễn Trãi, Quận 5, TP. Hồ Chí Minh');
INSERT INTO CUSTOMER (username, password, email, first_name, last_name, phone, address)
VALUES ('dangthikimcuc', 'cucy_2025', 'dangthikimcuc@example.com', 'Thị Kim Cúc', 'Đặng', '0945678901', '88 Trần Hưng Đạo, Quận 1, TP. Hồ Chí Minh');

INSERT INTO ADMIN (username, password, email, first_name, last_name, phone, address, hire_date, department)
VALUES ('adminngocanh', 'adminpass123', 'ngocanh.admin@example.com', 'Ngọc Anh', 'Trịnh', '0987654321', '15 Lý Thường Kiệt, Quận Hoàn Kiếm, Hà Nội', '2023-07-01', 'Quản trị hệ thống');
INSERT INTO ADMIN (username, password, email, first_name, last_name, phone, address, hire_date, department)
VALUES ('adminquanghuy', 'securepass2025', 'quanghuy.admin@example.com', 'Quang Huy', 'Bùi', '0978123456', '22 Nguyễn Huệ, Quận 1, TP. Hồ Chí Minh', '2022-03-15', 'Quản trị hệ thống');

INSERT INTO AUTHOR (first_name, last_name, birth_date, nationality)
VALUES ('Xuân', 'Diệu', '1916-02-02', 'Việt Nam');
INSERT INTO AUTHOR (first_name, last_name, birth_date, nationality)
VALUES ('Hàn', 'Mặc Tử', '1912-09-22', 'Việt Nam');
INSERT INTO AUTHOR (first_name, last_name, birth_date, nationality)
VALUES ('Nguyễn', 'Nhật Ánh', '1955-05-07', 'Việt Nam');

INSERT INTO BOOKS (title, isbn, description, publication_year, copies_available, author_id)
VALUES ('Thơ Xuân Diệu', '9786049980012', 'Tuyển tập thơ tình nổi tiếng của nhà thơ Xuân Diệu.', 1943, 5, 1);
INSERT INTO BOOKS (title, isbn, description, publication_year, copies_available, author_id)
VALUES ('Gửi Hương Cho Gió', '9786049980029', 'Tác phẩm thơ lãng mạn đặc trưng của Xuân Diệu.', 1945, 3, 1);
INSERT INTO BOOKS (title, isbn, description, publication_year, copies_available, author_id)
VALUES ('Đau Thương', '9786049980036', 'Tập thơ nổi tiếng thể hiện nỗi đau và khát vọng sống của Hàn Mặc Tử.', 1940, 4, 2);
INSERT INTO BOOKS (title, isbn, description, publication_year, copies_available, author_id)
VALUES ('Thơ Hàn Mặc Tử', '9786049980043', 'Tuyển tập những bài thơ đặc sắc của Hàn Mặc Tử.', 1942, 2, 2);
INSERT INTO BOOKS (title, isbn, description, publication_year, copies_available, author_id)
VALUES ('Kính Vạn Hoa', '9786049980050', 'Bộ truyện thiếu nhi nổi tiếng của Nguyễn Nhật Ánh.', 1995, 7, 3);
INSERT INTO BOOKS (title, isbn, description, publication_year, copies_available, author_id)
VALUES ('Cho Tôi Xin Một Vé Đi Tuổi Thơ', '9786049980067', 'Câu chuyện nhẹ nhàng và sâu sắc về tuổi thơ.', 2008, 6, 3);
INSERT INTO BOOKS (title, isbn, description, publication_year, copies_available, author_id)
VALUES ('Tôi Thấy Hoa Vàng Trên Cỏ Xanh', '9786049980074', 'Tiểu thuyết cảm động về tuổi thơ và tình anh em.', 2010, 8, 3);
INSERT INTO BOOKS (title, isbn, description, publication_year, copies_available, author_id)
VALUES ('Mắt Biếc', '9786049980081', 'Truyện tình cảm học trò sâu lắng của Nguyễn Nhật Ánh.', 1990, 5, 3);
INSERT INTO BOOKS (title, isbn, description, publication_year, copies_available, author_id)
VALUES ('Ngồi Khóc Trên Cây', '9786049980098', 'Câu chuyện đầy nhân văn về tình bạn và sự cảm thông.', 2013, 4, 3);
INSERT INTO BOOKS (title, isbn, description, publication_year, copies_available, author_id)
VALUES ('Bồ Câu Không Đưa Thư', '9786049980104', 'Một trong những truyện buồn và lặng lẽ của Nguyễn Nhật Ánh.', 2002, 3, 3);

INSERT INTO GENRE (genre_name, description)
VALUES ('Thơ', 'Thể loại văn học dùng hình thức ngắn gọn, ngôn ngữ cô đọng và hình ảnh giàu cảm xúc.');
INSERT INTO GENRE (genre_name, description)
VALUES ('Thiếu nhi', 'Sách dành cho trẻ em và thanh thiếu niên với nội dung gần gũi, dễ hiểu.');
INSERT INTO GENRE (genre_name, description)
VALUES ('Tiểu thuyết', 'Tác phẩm văn xuôi dài, phản ánh cuộc sống qua các nhân vật và sự kiện hư cấu.');

INSERT INTO BOOK_GENRE (book_id, genre_id) VALUES (1, 1);
INSERT INTO BOOK_GENRE (book_id, genre_id) VALUES (2, 1);
INSERT INTO BOOK_GENRE (book_id, genre_id) VALUES (3, 1);
INSERT INTO BOOK_GENRE (book_id, genre_id) VALUES (4, 1);
INSERT INTO BOOK_GENRE (book_id, genre_id) VALUES (5, 2);
INSERT INTO BOOK_GENRE (book_id, genre_id) VALUES (6, 2);
INSERT INTO BOOK_GENRE (book_id, genre_id) VALUES (7, 3);
INSERT INTO BOOK_GENRE (book_id, genre_id) VALUES (8, 3);
INSERT INTO BOOK_GENRE (book_id, genre_id) VALUES (9, 3);
INSERT INTO BOOK_GENRE (book_id, genre_id) VALUES (10, 3);

INSERT INTO RESERVATIONS (customer_id, book_id, reservation_date, status)
VALUES (1, 3, '2025-05-20', 'active');
INSERT INTO RESERVATIONS (customer_id, book_id, reservation_date, status)
VALUES (2, 7, '2025-05-18', 'fulfilled');
INSERT INTO RESERVATIONS (customer_id, book_id, reservation_date, status)
VALUES (3, 1, '2025-05-15', 'cancelled');
INSERT INTO RESERVATIONS (customer_id, book_id, reservation_date, status)
VALUES (4, 5, '2025-05-22', 'active');
INSERT INTO RESERVATIONS (customer_id, book_id, reservation_date, status)
VALUES (5, 10, '2025-05-19', 'fulfilled');

INSERT INTO BORROWING (customer_id, admin_id, bill_id, borrow_date, due_date, return_date, status, borrowing_fee, overdue_fee)
VALUES (1, 1, 101, '2025-05-01', '2025-05-15', '2025-05-14', 'returned', 10000.00, 0.00);
INSERT INTO BORROWING (customer_id, admin_id, bill_id, borrow_date, due_date, return_date, status, borrowing_fee, overdue_fee)
VALUES (2, 2, 102, '2025-05-05', '2025-05-19', NULL, 'borrowed', 12000.00, 0.00);
INSERT INTO BORROWING (customer_id, admin_id, bill_id, borrow_date, due_date, return_date, status, borrowing_fee, overdue_fee)
VALUES (3, 1, 103, '2025-04-20', '2025-05-04', '2025-05-10', 'returned', 8000.00, 3000.00);
INSERT INTO BORROWING (customer_id, admin_id, bill_id, borrow_date, due_date, return_date, status, borrowing_fee, overdue_fee)
VALUES (4, 2, 104, '2025-05-10', '2025-05-24', NULL, 'borrowed', 15000.00, 0.00);
INSERT INTO BORROWING (customer_id, admin_id, bill_id, borrow_date, due_date, return_date, status, borrowing_fee, overdue_fee)
VALUES (5, 1, 105, '2025-04-15', '2025-04-29', NULL, 'overdue', 11000.00, 5000.00);

INSERT INTO BILL (borrowing_id, total_amount)
VALUES (1, 10000.00);
INSERT INTO BILL (borrowing_id, total_amount)
VALUES (2, 12000.00);
INSERT INTO BILL (borrowing_id, total_amount)
VALUES (3, 11000.00);  -- borrowing_fee + overdue_fee from borrowing_id=3
INSERT INTO BILL (borrowing_id, total_amount)
VALUES (4, 15000.00);
INSERT INTO BILL (borrowing_id, total_amount)
VALUES (5, 16000.00);  -- borrowing_fee + overdue_fee from borrowing_id=5

INSERT INTO BOOK_BORROW (borrowing_id, book_id, quantity)
VALUES (1, 3, 1);
INSERT INTO BOOK_BORROW (borrowing_id, book_id, quantity)
VALUES (2, 7, 2);
INSERT INTO BOOK_BORROW (borrowing_id, book_id, quantity)
VALUES (3, 1, 1);
INSERT INTO BOOK_BORROW (borrowing_id, book_id, quantity)
VALUES (4, 5, 3);
INSERT INTO BOOK_BORROW (borrowing_id, book_id, quantity)
VALUES (5, 10, 1);

ALTER TABLE admin CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE author CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE Books CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE customer CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE genre CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;