Project Requirement Document (PRD)
Proje Adı: Yapay Zeka Destekli Mobil Spor Uygulaması
Hazırlayan: Berkay Alagöz
Danışman: Şahin Aydın
Bölüm: Enformasyon Teknolojileri
Teknoloji Yığını (Tech Stack):

Mobil Uygulama: Kotlin (Android)

Backend: Firebase (Authentication, Firestore, Storage)

AI Modeli: Python (TensorFlow, .tflite formatında model)

1. Proje Amacı ve Kapsamı
Günümüzde bireyler, plansız ve programsız egzersizlerden dolayı sağlık hedeflerine ulaşmakta zorlanmaktadır. Bu proje ile kullanıcıların yaş, kilo ve boy endeksine göre yapay zeka destekli spor ve beslenme programları sunan bir mobil uygulama geliştirilmesi amaçlanmaktadır.

2. Hedef Kullanıcı Kitlesi
Amatör sporcular

Spor yapmak isteyen ama profesyonel destek alamayan bireyler

Sağlıklı yaşam hedefi olan kullanıcılar

3. Temel Özellikler ve İşlevler
3.1 Kullanıcı Kayıt ve Giriş
Firebase Authentication ile email-parola temelli kayıt ve giriş

Firebase ile kullanıcı bilgileri (isim, yaş, boy, kilo, cinsiyet, sağlık durumu) alınır

3.2 Spor Branşı Seçimi
Kullanıcı, uygulamaya giriş yaptıktan sonra ilgilendiği spor branşını seçer (örneğin: fitness, pilates, koşu, yoga)

3.3 Beslenme Programı Atama
Seçilen branş ve kullanıcı bilgilerine göre statik (ön tanımlı) beslenme programı gösterilir

3.4 Antrenör Planlama
Kullanıcı, sistemdeki dijital (gerçek olmayan) antrenörlerden birini seçer

Günlük/haftalık plan oluşturabilir

3.5 Egzersiz Gösterimi
Egzersizler video ve görsel destekli olarak Firebase Storage üzerinden gösterilir

Antrenörler ile eşleşmiş branşa özel egzersiz programları

3.6 AI Destekli Kişiselleştirme
AI modeli, kullanıcının gelişim geçmişine göre haftalık önerilerde bulunur

Model girdileri: yaş, kilo, boy, haftalık antrenman sayısı, uyku ve beslenme verileri (opsiyonel)

Çıktı: egzersiz yoğunluğu ve tekrar önerisi

4. AI Modeli ve Veri İşleme Süreci
4.1 Veri Hazırlığı (Offline)
Kullanıcı profilleri ve gelişim verileri CSV formatında etiketlenir

Örnek kolonlar: age, weight, height, workout_frequency, goal, recommendation_intensity

4.2 Model Eğitimi
Python + TensorFlow ile Dense Neural Network eğitilir

Model çıktısı: önerilen egzersiz düzeyi

Eğitim sonrası model .tflite formatına çevrilir

4.3 Mobil Entegrasyon
Model Kotlin uygulamasının assets/ klasörüne eklenir

TensorFlow Lite API ile Android üzerinde inference yapılır

5. Firebase Kullanım Detayları
Modül	Teknoloji / Yöntem
Authentication	Email-password tabanlı giriş
Firestore DB	Kullanıcı verileri, antrenman geçmişi
Firebase Storage	Egzersiz videoları ve görselleri
Firebase Analytics	Kullanıcı davranış analizi (isteğe bağlı)

6. Kullanıcı Arayüzü (UI/UX) Özellikleri
Modern ve sade tasarım

Spor branşı ikonları

Haftalık plan takvimi

Gelişim grafikleri

Egzersiz videolarına erişim kartları

7. Gereksinimler
7.1 Fonksiyonel Gereksinimler
Kayıt ve giriş sistemi

Spor branşı ve antrenör seçimi

AI tahmin sonucu sunulması

Program önerilerinin gösterimi

7.2 Fonksiyonel Olmayan Gereksinimler
Uygulama düşük donanımlı cihazlarda da çalışmalı

Tüm kullanıcı verileri güvenli şekilde saklanmalı

İnternet bağlantısı gerektiren servisler (Firestore, Storage) çevrimdışı uyarısı vermeli

8. Gelecekteki Genişletmeler
Gerçek antrenörlerle görüntülü seans

Apple Health / Google Fit entegrasyonu

NLP tabanlı sohbet botu (antrenör yerine)