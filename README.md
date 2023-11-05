<a id="readme-top"></a>





<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/HuynhThaiHCMUT/FileTransfer">
    <img src="hcmut.png" alt="Logo" width="160" height="160">
  </a>

<h3 align="center">File Sharing Application</h3>

  <p align="center">
    Một ứng dụng chia sẻ file được hiện thực sử dụng giao thức tầng ứng dụng tự định nghĩa dựa trên TCP/IP.
    <br />
    <a href="#getting-started"><strong>Xem hướng dẫn »</strong></a>
    <br />
    <br />
    <a href="https://github.com/HuynhThaiHCMUT/FileTransfer/releases">Tải xuống</a>
    ·
    <a href="mailto:phuong.ngo0320@hcmut.edu.vn">Báo lỗi</a>
    ·
    <a href="mailto:phuong.ngo0320@hcmut.edu.vn">Đề xuất tính năng</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Mục lục</summary>
  <ol>
    <li>
      <a href="#about-the-project">Về dự án này</a>
      <ul>
        <li><a href="#built-with">Công nghệ sử dụng</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Bắt đầu</a>
      <ul>
        <li><a href="#prerequisites">Điều kiện</a></li>
        <li><a href="#installation">Cài đặt</a></li>
      </ul>
    </li>
    <li>
      <a href="#usage">Hướng dẫn sử dụng</a>
      <ul>
        <li><a href="#notes">Một số lưu ý</a></li>
        <li><a href="#functions">Thao tác trong ứng dụng</a></li>
        <li><a href="#errors">Lỗi có thể xảy ra khi sử dụng</a></li>
      </ul>
    </li>
    <li><a href="#contact">Liên hệ</a></li>
    <li><a href="#acknowledgments">Tài liệu tham khảo</a></li>
  </ol>
</details>





<!-- ABOUT THE PROJECT -->
<a id="about-the-project"></a>

## Về dự án này

Ứng dụng này được hiện thực để phục vụ cho môn học Mạng máy tính, thuộc về Trường Đại học Bách khoa, ĐHQG TP.HCM. Dự án hướng đến mục tiêu xây dựng nên một hệ thống chia sẻ file dựa theo kiến trúc kết hợp giữa Client-Server và Peer-to-Peer, nhằm tối ưu hóa hiệu suất của hệ thống. Ứng dụng này dành cho phía Client, người có thể đăng tải file lên server và tải xuống các file từ server.

Link dự án: 

- Client Application: [https://github.com/HuynhThaiHCMUT/FileTransfer](https://github.com/HuynhThaiHCMUT/FileTransfer)
- Server Application: [https://github.com/HuynhThaiHCMUT/FileTransferServer](https://github.com/HuynhThaiHCMUT/FileTransferServer)

<p align="right">(<a href="#readme-top">back to top</a>)</p>


### Công nghệ sử dụng
<a id="built-with"></a>

- [Java](https://www.java.com)
- [JavaFX](https://openjfx.io)
- [Apache Maven](https://maven.apache.org)

<p align="right">(<a href="#readme-top">back to top</a>)</p>





<!-- GETTING STARTED -->
<a id="getting-started"></a>

## Bắt đầu

<a id="prerequisites"></a>
### Điều kiện

Để sử dụng ứng dụng này, bạn cần cài đặt ngôn ngữ Java phiên bản mới nhất.

Cài đặt Java tại đây: [Java Downloads](https://www.oracle.com/java/technologies/downloads).

<a id="installation"></a>
### Cài đặt

1. Truy cập đường dẫn sau: [Download FileTransfer](https://github.com/HuynhThaiHCMUT/FileTransfer/releases)
2. Tải về file `FileTransfer.exe`
3. Nhấn đúp vào `FileTransfer.exe` để bắt đầu sử dụng

<p align="right">(<a href="#readme-top">back to top</a>)</p>





<!-- USAGE -->
<a id="usage"></a>

## Hướng dẫn sử dụng

<a id="notes"></a>

### Một số lưu ý

- Để sử dụng ứng dụng này, bạn cần có một kết nối internet ổn định.
- Trước khi sử dụng, bạn cần đăng nhập vào ứng dụng hoặc đăng ký nếu chưa có tài khoản

  - Các bước đăng ký:

    1. Điền thông tin username cho tài khoản mới tại ô "Username"
    2. Điền địa chỉ IP của server cần kết nối tại ô "Server IP"
    3. Nhấn vào "Sign up"

  - Các bước đăng nhập:

    1. Điền thông tin username của tài khoản tại ô "Username"
    2. Điền địa chỉ IP của server cần kết nối tại ô "Server IP"
    3. Nhấn vào "Log in"

<a id="functions"></a>

### Thao tác trong ứng dụng

Bạn có thể sử dụng ứng dụng này thông qua giao diện GUI hoặc CLI.

#### Sử dụng giao diện GUI

Giao diện sau khi đăng nhập gồm có các tab sau: User, File, Search và Terminal. Trong đó 3 tab đầu cung cấp giao diện GUI cho người dùng.

- User: Nhấn vào "Sign out" nếu bạn cần đăng xuất.

- File: Đây là nơi hiển thị các file mà bạn đã đăng tải lên server. Bạn có thể đăng tải file lên server tại đây.

  - Tìm kiếm: Nhập tên file vào thanh "Search" để tìm kiếm file.

  - Đăng tải file: 
  
    - Nhấn vào "Upload" ở phía dưới bên phải để đăng tải file.

    - Chọn file từ máy của bạn trong hộp thoại "Select file to upload"

    - Màn hình sẽ hiển thị hộp thoại để nhập thông tin file. Bạn cần điền tên file khi lưu trên server và mô tả (tùy chọn) để tiếp tục.

    - Nhấn "OK" để hoàn tất.

  - Refresh: Trong trường hợp bạn thay đổi tên hoặc đường dẫn tới file mà bạn đã đăng, file sẽ tự động bị xóa khỏi server. Bạn có thể nhấn "Refresh" để cập nhật lại danh sách file sau thao tác trên.

- Search: Đây là nơi hiển thị các file có sẵn trên server. Bạn có thể tải xuống file từ server tại đây.

  - Tìm kiếm: Nhập tên file vào thanh "Search" để tìm kiếm file.

  - Tải xuống file:

    - Nhấn đúp vào một file trong danh sách file được hiển thị

    - Chọn nơi để lưu file trong hộp thoại "Save as"

    - Nhấn "OK" để hoàn tất

#### Sử dụng giao diện CLI

Bạn có thể thao tác bằng CLI thông qua tab "Terminal". Giao diện này cung cấp các lệnh như sau:

| Cú pháp | Chức năng | Mô tả | Ví dụ |
|-|-|-|-|
| `help`  | Trợ giúp | Hiển thị danh sách các lệnh có sẵn cùng với cú pháp và cách sử dụng, dùng trong trường hợp bạn quên | | |
| `start` | Khởi động Network Listener    | Lệnh để test | |
| `stop`  | Tạm dừng Network Listener     | Lệnh để test | |
| `clear` | Xóa toàn bộ nội dung terminal | Terminal sẽ được reset lại như ban đầu | |
| `publish "<local name>" "<upload name>"` |  Đăng tải file | Thay `<local name>` bằng đường dẫn tới file cần đăng, thay `<upload name>` bằng tên của file đó khi lưu trên server | `publish "D:\abc.txt" "xyz.txt"` |
| `fetch "<filename>"` | Tìm kiếm file | Liệt kê danh sách file có tên (có bao gồm extension) chứa chuỗi `<filename>`, mỗi file hiển thị sẽ có index tương ứng | `fetch "xy"` |
| `download "<index>" "<save location>"` | Tải xuống file | Thay `<index>` bằng giá trị index có được từ lệnh `fetch` tương ứng với file cần tải, thay `<save location>` bằng đường dẫn tới nơi lưu file cần tải (có bao gồm tên file khi lưu) | `download "0" "D:\local-name.txt"` |

<a id="errors"></a>

### Lỗi có thể xảy ra khi sử dụng

<!-- TODO: add error cases -->
...

..

.

<p align="right">(<a href="#readme-top">back to top</a>)</p>





<!-- CONTACT -->
<a id="contact"></a>

## Liên hệ

Thành viên của dự án:

- Đinh Huỳnh Thái
- Lê Thanh Tùng 
- Nguyễn Thị Xuân Hoa
- Ngô Văn Phương - phuong.ngo0320@hcmut.edu.vn

<p align="right">(<a href="#readme-top">back to top</a>)</p>





<!-- ACKNOWLEDGMENTS -->
<a id="acknowledgments"></a>

## Tài liệu tham khảo

* Kurose, J. and Ross, K. (2022). _Computer Networking: a top-down approach, 8th edition_. Boston: Pearson Education Limited.
* [JDK21 Documentation](https://docs.oracle.com/en/java/javase/21)

<p align="right">(<a href="#readme-top">back to top</a>)</p>