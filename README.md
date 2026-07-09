# OrderReady

Depo, üretim planlama ve tedarikçi süreçlerini birleştiren, reçete tabanlı stok ve sipariş planlama sistemi.

## Problem ve Amaç

Poşet üretimi yapan bir fabrikada, her ürünün kendine özgü bir reçetesi vardır: belirli miktarda hammadde ve ambalaj malzemesi kullanılarak üretilir. Bu sistemde üç ayrı süreç birbirinden kopuk ilerliyordu:

- **Depo**, elindeki hammadde ve ambalaj stoğunu güncel tutmakta zorlanıyor.
- **Tedarik**, hangi malzemenin hangi firmadan, ne kadar sürede geldiği net değil.
- **Üretim planlama**, bir müşteri talebi geldiğinde ("50 kg X ürününden istiyorum") elde yeterli malzeme olup olmadığını, eksikse ne kadar süre sonra tedarik edilebileceğini ve toplamda ne zaman teslimat yapılabileceğini hızlıca hesaplayamıyor.

**OrderReady**, bu üç süreci tek bir sistemde birleştirerek:
- Depo çalışanlarının güncel stok bilgisi girmesini sağlar,
- Hangi hammaddenin hangi tedarikçiden geldiğini ve tedarik süresini kayıt altına alır,
- Üretim planlamacının bir ürün ve miktar girerek, gerekli hammaddelerin yeterli olup olmadığını, eksikse tedarik süresini, üretim süresini ve toplam teslimat süresini anında görmesini sağlar.

Amaç: malzeme siparişini kolaylaştırmak, depo ile üretim planlama arasında bir köprü kurmak, ve müşteri talebine hızlı ve net bir teslimat tarihi ile geri dönüş yapabilmek.

## Kullanılacak Teknolojiler

- **Backend:** Java 17 / Spring Boot
- **Veritabanı:** MySQL
- **Frontend:** React
- **Veri Görselleştirme / Arayüz:** React bileşenleri ile form ve tablo tabanlı ekranlar

## Kapsam 

- Ürün (poşet türü) ve reçete yönetimi
- Hammadde/ambalaj malzemesi ve stok takibi (depo güncellemesi)
- Tedarikçi ve tedarik süresi yönetimi
- Sipariş hesaplama motoru: ürün + miktar girildiğinde eksik malzeme, tedarik süresi, üretim süresi ve toplam süre hesaplama

## Kapsam Dışı ( ileride eklenebilir)

- Tedarikçilere otomatik e-posta gönderimi
- Çoklu depo/lokasyon desteği
- Maliyet hesaplama ve faturalandırma
