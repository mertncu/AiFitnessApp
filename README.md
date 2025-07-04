# AI Fitness App

AI Fitness App, yapay zeka destekli kişiselleştirilmiş fitness deneyimi sunan modern bir Android uygulamasıdır. Google'ın Gemini AI teknolojisini kullanarak kullanıcılara özel antrenman programları, beslenme önerileri ve motivasyon desteği sağlar.

##  Özellikler

- **Kişiselleştirilmiş Antrenman Programları**: Gemini AI ile kullanıcının yaş, kilo, boy, fitness hedefi ve deneyim seviyesine göre özelleştirilmiş antrenman programları
- **Fitness Analizi**: Detaylı vücut analizi ve fitness seviyesi değerlendirmesi
- **Günlük Motivasyon**: AI destekli kişiselleştirilmiş motivasyon mesajları
- **Çevrimdışı Çalışma**: Firebase Firestore ile gelişmiş çevrimdışı veri desteği
- **Modern UI/UX**: Jetpack Compose ile geliştirilmiş modern ve kullanıcı dostu arayüz
- **Güvenli Kimlik Doğrulama**: Firebase Authentication ile güvenli kullanıcı yönetimi

## 🛠 Tech Stack

### Backend & AI
- **Google Gemini AI**: Kişiselleştirilmiş antrenman ve beslenme önerileri için AI modeli
- **Firebase**
  - Authentication: Kullanıcı kimlik doğrulama
  - Firestore: Veri depolama ve senkronizasyon
  - Storage: Medya dosyaları depolama

### Frontend
- **Jetpack Compose**: Modern UI geliştirme
- **Material Design 3**: UI/UX tasarım sistemi
- **Coil**: Görsel yükleme ve önbellekleme
- **Navigation Compose**: Uygulama içi navigasyon

### Diğer Teknolojiler
- **Retrofit & OkHttp**: API iletişimi
- **Kotlin Coroutines**: Asenkron işlemler
- **Kotlin Serialization**: JSON işleme
- **AndroidX Lifecycle**: Yaşam döngüsü yönetimi

## 🏗Proje Yapısı

```
app/
├── model/          # Veri modelleri
├── service/        # AI ve veri servisleri
└── ui/
    ├── assessment/ # Kullanıcı değerlendirme ekranları
    ├── auth/       # Kimlik doğrulama
    ├── diet/       # Beslenme önerileri
    ├── main/       # Ana ekranlar
    └── theme/      # UI tema ve stil
```

##  Başlangıç

### Gereksinimler
- Android Studio Hedgehog | 2023.1.1 veya üzeri
- JDK 11
- Android SDK 35
- Google Cloud hesabı (Gemini API için)
- Firebase projesi

### Kurulum

1. Projeyi klonlayın:
```bash
git clone https://github.com/mertncu/AiFitnessApp.git
```

2. `local.properties` dosyasına Gemini API anahtarınızı ekleyin:
```properties
GEMINI_API_KEY=your_api_key_here
```

3. Firebase yapılandırma dosyasını (`google-services.json`) `app` klasörüne ekleyin.

4. Projeyi Android Studio ile açın ve sync edin.

## 📱 Ekran Görüntüleri

<img src="https://github.com/user-attachments/assets/82bce763-00ce-49a2-9a65-86b447232a6c" width="300"/>
<img src="https://github.com/user-attachments/assets/ba6c776f-a8b6-4af8-a12a-3565b882d0d6" width="300"/>
<img src="https://github.com/user-attachments/assets/f4c428e7-56d0-4be2-a2f5-e0b2e6a9a8bf" width="300"/>

## 🔐 Güvenlik

- Tüm API anahtarları güvenli bir şekilde saklanır
- Firebase Authentication ile güvenli kullanıcı yönetimi
- Hassas veriler şifrelenerek depolanır
- Çevrimdışı veri güvenliği için Firestore güvenlik kuralları

## 🤝 Katkıda Bulunma

1. Fork edin
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Değişikliklerinizi commit edin (`git commit -m 'feat: Add amazing feature'`)
4. Branch'inizi push edin (`git push origin feature/amazing-feature`)
5. Pull Request oluşturun

## ⚠️ Önemli Güvenlik Notu

Bu projeyi çalıştırmak için aşağıdaki dosyaları manuel olarak eklemeniz gerekmektedir:

1. `app/google-services.json` - Firebase yapılandırma dosyası
2. `local.properties` dosyasında:
   ```properties
   GEMINI_API_KEY=your_api_key_here
   ```

**Bu dosyalar güvenlik nedeniyle git reposuna dahil edilmemiştir. Lütfen kendi API anahtarlarınızı ve yapılandırma dosyalarınızı kullanın.**
