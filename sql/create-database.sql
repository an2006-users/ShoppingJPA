-- Chạy bằng SQL Server Management Studio, chỉ cần tạo database.
-- Các bảng categories / videos sẽ được Hibernate tự tạo (hibernate.hbm2ddl.auto=update)
-- khi chạy ứng dụng hoặc chạy vn.iotstar.config.Test.
IF DB_ID('ShoppingJPA') IS NULL
    CREATE DATABASE ShoppingJPA;
GO

-- Dự phòng: nếu Hibernate không tự tạo được bảng, chạy thủ công đoạn dưới đây.
USE ShoppingJPA;
GO
IF OBJECT_ID('categories', 'U') IS NULL
    CREATE TABLE categories (
        categoryId   INT IDENTITY(1,1) PRIMARY KEY,
        categoryname NVARCHAR(255) NOT NULL,
        images       NVARCHAR(255) NULL,
        status       INT NOT NULL DEFAULT 1
    );
GO
IF OBJECT_ID('videos', 'U') IS NULL
    CREATE TABLE videos (
        videoId     NVARCHAR(255) PRIMARY KEY,
        active      BIT NOT NULL DEFAULT 0,
        description NVARCHAR(MAX) NULL,
        poster      NVARCHAR(255) NULL,
        title       NVARCHAR(255) NULL,
        views       INT NOT NULL DEFAULT 0,
        categoryId  INT NULL REFERENCES categories(categoryId)
    );
GO
IF OBJECT_ID('users', 'U') IS NULL
    CREATE TABLE users (
        userId      INT IDENTITY(1,1) PRIMARY KEY,
        username    NVARCHAR(50)  NOT NULL UNIQUE,
        password    NVARCHAR(255) NOT NULL,
        fullname    NVARCHAR(100) NOT NULL,
        email       NVARCHAR(100) NOT NULL UNIQUE,
        createdDate DATETIME2     NULL
    );
GO
