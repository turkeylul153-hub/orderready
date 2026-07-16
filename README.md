cat > README.md << 'EOF'
# OrderReady

Depo, üretim planlama ve satış süreçlerini birleştiren, sipariş ve reçete tabanlı stok yönetim sistemi.

## Problem ve Amaç

Poşet üretimi yapan bir fabrikada, depo, üretim planlama ve satış süreçleri birbirinden kopuk ilerliyordu. OrderReady, bu süreçleri tek bir sistemde birleştirir:

- **Satış**, yeni müşteri siparişlerini sisteme girer
- **Üretim Planlama**, gelen siparişleri görür, üretime başlatır ve tamamlar
- **Depo**, hammadde ve bitmiş ürün stoğunu yönetir, gönderim yapar

Sipariş durumu değiştikçe (üretime başla → tamamlandı → gönderildi), ilgili stok hareketleri **otomatik olarak** kaydedilir. Tüm stok hareketleri işlem bazlı tutulur — hiçbir kayıt silinmez veya değiştirilmez, sadece yeni düzeltme kayıtları eklenir. Bu sayede geçmişteki herhangi bir tarih için "o an ne kadar stok vardı" sorusuna her zaman net cevap verilebilir.

## Kullanılacak Teknolojiler

- **Backend:** Java 17 / Spring Boot
- **Veritabanı:** MySQL
- **Frontend:** React
- **Kimlik Doğrulama:** BCrypt ile şifrelenmiş, rol tabanlı giriş sistemi (Depo / Satış / Üretim Planlama)

## Kapsam (v1)

- Ürün ve reçete (BOM) yönetimi
- Rol tabanlı giriş sistemi
- Satış: yeni sipariş oluşturma
- Üretim planlama: sipariş listesi, üretime başlatma, tamamlama
- Depo: hammadde/ürün stok takibi, elle ekleme/çıkarma, gönderim
- transaction-based stok geçmişi

## Kapsam Dışı (v1 için, ileride eklenebilir)

- Çoklu depo desteği (altyapı hazır, arayüz yok)
- Düşük stok otomatik bildirimi (e-posta/SMS)
- Raporlama ve analitik ekranları

