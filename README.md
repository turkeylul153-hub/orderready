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

## Kullanıcı Rolleri ve Yetki Yönetimi

- **WAREHOUSE** (Depo)
- **SALES** (Satış)
- **PLANNER** (Üretim Planlama)
- **ADMIN** (Kullanıcı Yönetimi)

Bir kullanıcı, ihtiyaç halinde birden fazla role sahip olabilir (örn. "WAREHOUSE,PLANNER"). Çoklu rollü kullanıcılar, giriş sonrası ekranın üstünde her rol için ayrı bir kart görür, kartlara tıklayarak aralarında geçiş yapabilir.

**Yetki talep sistemi:** Bir kullanıcı, sahip olmadığı bir role (soluk, kilitli görünen bir kart olarak) tıklayarak erişim talep edebilir. Talep, ADMIN rolüne sahip bir kullanıcının panelinde "Bekleyen Yetki Talepleri" olarak görünür; onaylanırsa kullanıcının rolü otomatik güncellenir, reddedilirse hiçbir değişiklik yapılmaz. ADMIN, ayrıca kullanıcı yönetim panelinden doğrudan rol ekleyip çıkarabilir.

## Kapsam

### Kimlik Doğrulama ve Güvenlik
- BCrypt ile şifrelenmiş, rol tabanlı giriş sistemi
- Token tabanlı oturum yönetimi — sayfa yenilenince oturum korunur
- Tüm API uçlarında token doğrulaması (giriş uçları hariç)
- PIN korumalı geri alma işlemleri (üretimi geri alma, sevkiyatı geri alma)
- Sipariş iptali için zorunlu açıklama + PIN

### Satış
- Yeni sipariş oluşturma
- Ürün/miktar girilirken otomatik, gecikmeli (debounce) uygunluk kontrolü — eksik malzeme varsa eksikliğin büyüklüğüne göre kademeli teslim tahmini
- Depoda hazır, müsait ürün varsa bunu anında gösterir ("Depoda yeterli ürün var")
- Sipariş listesi, sayfalama

### Üretim Planlama
- Sipariş listesi: durum filtreleme, arama, öncelik sıralaması (acil önce)
- Sipariş detayında malzeme ihtiyacı ve tedarikçi bilgisi
- Stoktan karşılama: Sipariş oluşturulurken, depoda gerçekten müsait (başka bir siparişe henüz ayrılmamış) ürün varsa, sipariş üretime hiç gerek kalmadan otomatik tamamlanır ve direkt Depo'nun sevkiyat listesine düşer
- Depo tam karşılamıyorsa, sadece eksik kalan miktar için üretim yapılır (hammadde sadece eksik miktar kadar düşer)
- PENDING durumundaki, hiç işlem geçmişi olmayan siparişler silinebilir
- İşlem geçmişi olan siparişler iptal edilebilir (silinmez, geçmiş korunur)
- Malzeme/tedarikçi yönetim paneli (SQL'e gerek kalmadan ekleme)

### Depo
- Hammadde stok takibi, elle ekleme/çıkarma, düşük stok eşiğinin altına düşen malzemeler kırmızı vurgulanır
- Ürün stoğu takibi — hem sipariş üretiminden hem elle eklemeden gelen stok tek yerde görünür, tükenmiş ürünler vurgulanır
- Ürün ve malzeme stok hareketleri geçmişi, ayrı ayrı, sayfalanabilir
- Sevkiyat yapma, sevkiyatı PIN ile geri alma
- Malzeme stok hareketleri geçmişi — malzeme adı ve hareket türüne göre filtrelenebilir

### Kullanıcı Yönetimi (ADMIN)
- Tüm kullanıcıları ve rollerini listeleme
- Kullanıcılara doğrudan rol ekleme/çıkarma
- Bekleyen yetki taleplerini görüntüleme, onaylama veya reddetme

### Genel
- Transaction bazlı (event sourcing) stok geçmişi — hiçbir kayıt silinmez
- Toast bildirimleri (başarı/hata) — tüm sayfalarda tutarlı
- Responsive tasarım — telefon genişliğinde tablolar kart formatına dönüşür
- Sayfalama (pagination) — sipariş ve stok geçmişi listelerinde
- Durum ve öncelik etiketleri Türkçe gösterilir
