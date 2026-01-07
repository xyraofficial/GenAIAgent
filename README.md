# GenAI Agent

## Overview

GenAI Agent is an Android AI assistant application built with Material Design 3 (Material You) principles.

## Build Process

Aplikasi ini diatur untuk build otomatis menggunakan **GitHub Actions**. Anda tidak memerlukan Android Studio di komputer lokal untuk membuat file APK.

### Cara Mendapatkan APK:
1. Hubungkan project ini ke repositori **GitHub**.
2. Push perubahan ke branch `main`.
3. Buka tab **Actions** di repositori GitHub Anda.
4. Pilih workflow **Android CI**.
5. Tunggu proses build selesai (sekitar 2-3 menit).
6. Di bagian bawah halaman (setelah build sukses), Anda akan menemukan **Artifacts**. Klik `app-debug` untuk mengunduh file APK-nya.

## Project Architecture

### Design Framework
- **Material Design 3**: Menggunakan Dynamic Theming dan komponen Material 3 terbaru untuk tampilan yang modern dan responsif.
- **UI/UX**: Desain yang bersih dan intuitif, dioptimalkan untuk percakapan AI (seperti Gemini dan ChatGPT).

## System Architecture

### Screen Architecture
1. **Login/Register**: Sistem autentikasi lengkap dengan desain Material 3 yang elegan.
2. **Main Chat**: Antarmuka chat interaktif dengan input yang mudah digunakan dan tampilan pesan yang rapi.

### External Dependencies
- **Supabase**: Backend service untuk manajemen user dan database.
- **OkHttp**: Library handal untuk request API.
- **Material Components**: Library inti dari Google untuk desain Material You.