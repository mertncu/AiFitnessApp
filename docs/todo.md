### 1. Başlangıç Aşaması ✅

- [x]  1.1 - Android Studio projesini başlat (Empty Compose Activity)
- [x]  1.2 - Minimum SDK: 21+
- [x]  1.3 - Firebase projesini oluştur ve bağla
    - [x]  1.3.1 - Firebase Authentication
    - [x]  1.3.2 - Firebase Realtime Database veya Firestore
    - [x]  1.3.3 - Firebase Storage (video/görsel içerikler için)

---

### 2. Kullanıcı Yönetimi ✅

- [x]  2.1 - Giriş / Kayıt ekranlarını oluştur
    - [x]  2.1.1 - Modern UI tasarımı
    - [x]  2.1.2 - Email/Şifre giriş formu
    - [x]  2.1.3 - Sosyal medya bağlantıları
    - [x]  2.1.4 - Şifremi unuttum özelliği
- [x]  2.2 - Firebase Authentication ile email-password kayıt sistemi kur
- [x]  2.3 - Kullanıcı kayıt sonrası Firestore'da temel profil bilgilerini sakla
    - [ ]  2.3.1 - Ad, soyad
    - [ ]  2.3.2 - Boy, kilo, yaş
    - [ ]  2.3.3 - Branş, hedef, sağlık durumu
- [ ]  2.4 - Kullanıcı profil düzenleme ekranı oluştur

---

### 3. AI Modeli (Python tarafı)

- [ ]  3.1 - Eğitim verisi hazırla (CSV)
- [ ]  3.2 - TensorFlow ile model eğitimi yap (Dense Neural Network)
- [ ]  3.3 - Eğitilen modeli `.tflite` formatına dönüştür
- [ ]  3.4 - Modeli `assets` klasörüne ekle

---

### 4. AI Model Entegrasyonu (Kotlin)

- [ ]  4.1 - TensorFlow Lite kütüphanesini projeye dahil et
- [ ]  4.2 - Model giriş/çıkış yapısını tanımla (boy, kilo, yaş, branş, hedef vs.)
- [ ]  4.3 - Kullanıcı profilinden verileri alarak AI tahmini yap
- [ ]  4.4 - Tahmin sonucuna göre egzersiz programını Firestore'dan getir

---

### 5. Egzersiz Modülü

- [ ]  5.1 - "Bugünkü Egzersizler" ekranı hazırla
- [ ]  5.2 - Egzersiz kartları: Görsel, başlık, açıklama, süre
- [ ]  5.3 - Firebase Storage'dan video/görsel indirme
- [ ]  5.4 - Egzersiz tamamlama butonu ve Firestore'a günlük kayıt

---

### 6. Beslenme Modülü

- [ ]  6.1 - Kişiye özel sabit öneriler: Firestore'dan çek
- [ ]  6.2 - Günlük beslenme ekranı tasarla
- [ ]  6.3 - Sağlık durumuna göre alternatif öneriler ekle

---

### 7. Gelişim Takibi

- [ ]  7.1 - Kullanıcının haftalık egzersiz tamamlama oranını göster
- [ ]  7.2 - LineChart veya ProgressBar ile görsel analiz
- [ ]  7.3 - Gelişim geribildirimi: AI'dan basit yorumlar

---

### 8. Bildirim Sistemi ve Sanal Antrenör

- [ ]  8.1 - Firebase Cloud Messaging ile günlük egzersiz hatırlatması
- [ ]  8.2 - Sanal antrenör için motivasyon cümleleri (Firestore'dan rastgele çek)
- [ ]  8.3 - Kullanıcıya AI tabanlı kişisel motivasyon mesajı üret (opsiyonel)

---

### Tamamlanan Özellikler ✅
1. Modern ve temiz arayüz tasarımı
2. Firebase entegrasyonu
3. Giriş/Kayıt ekranları
4. Email/Şifre doğrulama
5. Sosyal medya bağlantıları
6. Şifremi unuttum özelliği
7. Hata mesajları ve validasyonlar