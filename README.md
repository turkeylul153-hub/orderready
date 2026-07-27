# OrderReady

Depo, üretim planlama ve satış süreçlerini birleştiren, sipariş ve reçete tabanlı stok yönetim sistemi.

## Problem ve Amaç

Poşet üretimi yapan bir fabrikada, depo, üretim planlama ve satış süreçleri birbirinden kopuk ilerliyordu. OrderReady, bu süreçleri tek bir sistemde birleştirir:

- **Satış**, yeni müşteri siparişlerini sisteme girer, stok durumuna göre anlık teslim tahmini görür
- **Üretim Planlama**, gelen siparişleri görür, üretime başlatır ve tamamlar
- **Depo**, hammadde ve bitmiş ürün stoğunu yönetir, gönderim yapar

Sipariş durumu değiştikçe (üretime başla → tamamlandı → gönderildi), ilgili stok hareketleri **otomatik olarak** kaydedilir. Tüm stok hareketleri işlem bazlı tutulur — hiçbir kayıt silinmez veya değiştirilmez, sadece yeni düzeltme kayıtları eklenir. Bu sayede geçmişteki herhangi bir tarih için "o an ne kadar stok vardı" sorusuna her zaman net cevap verilebilir.

## Kullanılan Teknolojiler

- **Backend:** Java 17 / Spring Boot, Spring Data JPA
- **Veritabanı:** MySQL
- **Frontend:** React (Vite)
- **Kimlik Doğrulama:** BCrypt şifreleme + token tabanlı oturum yönetimi
- **Yetkilendirme:** Her API isteğinde token doğrulayan merkezi filter (TokenFilter)

## Kullanıcı Rolleri

- **WAREHOUSE** (Depo)
- **SALES** (Satış)
- **PLANNER** (Üretim Planlama)

Bir kullanıcı, ihtiyaç halinde birden fazla role sahip olabilir (örn. "WAREHOUSE,PLANNER"). Çoklu rollü kullanıcılar, giriş sonrası ekranın üstünde her rol için ayrı bir kart görür, kartlara tıklayarak aralarında geçiş yapabilir. (Yetki ekleme/çıkarma arayüzü geliştirme aşamasında.)

## Kapsam (Güncel)

### Kimlik Doğrulama ve Güvenlik
- BCrypt ile şifrelenmiş, rol tabanlı giriş sistemi
- Token tabanlı oturum yönetimi — sayfa yenilenince oturum korunur
- Tüm API uçlarında token doğrulaması (giriş uçları hariç)
- PIN korumalı geri alma işlemleri (üretimi geri alma, sevkiyatı geri alma)
- Sipariş iptali için zorunlu açıklama + PIN

### Satış
- Yeni sipariş oluşturma
- Ürün/miktar girilirken otomatik, gecikmeli (debounce) uygunluk kontrolü — eksik malzeme varsa eksikliğin büyüklüğüne göre kademeli teslim tahmini
- Sipariş listesi, sayfalama

### Üretim Planlama
- Sipariş listesi: durum filtreleme, arama, öncelik sıralaması (acil önce)
- Sipariş detayında malzeme ihtiyacı ve tedarikçi bilgisi
- Üretime başlatma (stok kontrolü + otomatik düşüş), tamamlama
- PENDING durumundaki, hiç işlem geçmişi olmayan siparişler silinebilir
- İşlem geçmişi olan siparişler iptal edilebilir (silinmez, geçmiş korunur)
- Malzeme/tedarikçi yönetim paneli (SQL'e gerek kalmadan ekleme)

### Depo
- Hammadde/ürün stok takibi, elle ekleme/çıkarma
- Sevkiyat yapma, sevkiyatı PIN ile geri alma
- Stok hareketleri geçmişi — malzeme adı ve hareket türüne göre filtrelenebilir, sayfalanabilir

### Genel
- Transaction bazlı (event sourcing) stok geçmişi — hiçbir kayıt silinmez
- Toast bildirimleri (başarı/hata) — tüm sayfalarda tutarlı
- Responsive tasarım — telefon genişliğinde tablolar kart formatına dönüşür
- Sayfalama (pagination) — sipariş ve stok geçmişi listelerinde

## Kapsam Dışı (v1 için, ileride eklenebilir)

- Çoklu depo desteği (altyapı hazır, arayüz yok)
- Yetki ekleme/çıkarma arayüzü (şu an roller elle veritabanında atanıyor, geliştirme aşamasında)
- Düşük stok otomatik bildirimi (e-posta/SMS)
- Tedarikçiye otomatik sipariş/mail gönderimi
- Raporlama ve analitik ekranları
- Ürün stoğu için ayrı bir görüntüleme ekranı
