# Cronobat - Cronómetro y Temporizador con Música de Fondo

Una aplicación Android moderna para cronómetro y temporizador, desarrollada con Kotlin y Material Design, que incluye música de fondo ambiental.

## 🎯 Características Principales

- **⏱️ Temporizador**: Establece un tiempo personalizado (hasta 1 hora) con cuenta regresiva
- **⏰ Cronómetro**: Cuenta el tiempo hacia adelante con precisión
- **🎵 Música de Fondo**: Sonidos ambientales relajantes (lluvia, fuego, bosque, océano, ruido blanco)
- **🔄 Cambio de Modo**: Alterna fácilmente entre temporizador y cronómetro
- **📱 Diseño Adaptativo**: Se adapta automáticamente a orientación horizontal y vertical
- **🎨 Material Design**: Interfaz moderna y atractiva con Material Components 3

## 🚀 Funcionalidades de la Aplicación

### Temporizador
- **SeekBar intuitivo**: Desliza para establecer la duración del temporizador
- **Rango de tiempo**: Desde 1 segundo hasta 1 hora (3600 segundos)
- **Controles**: Iniciar, pausar y reiniciar
- **Notificación**: Alerta cuando el tiempo se completa

### Cronómetro
- **Conteo ascendente**: Cuenta el tiempo transcurrido
- **Precisión**: Actualización cada segundo
- **Controles**: Iniciar, pausar y reiniciar
- **Formato de tiempo**: Muestra horas:minutos:segundos

### Música de Fondo
- **5 tipos de sonidos**: Lluvia, fuego, bosque, océano, ruido blanco
- **Control de reproducción**: Iniciar/pausar música
- **Cambio de sonido**: Alterna entre diferentes ambientes
- **Reproducción continua**: Ideal para relajación y concentración

### Interfaz Adaptativa
- **Orientación vertical**: Layout optimizado para móviles
- **Orientación horizontal**: Layout mejorado para tablets y pantallas anchas
- **Responsive**: Se adapta automáticamente al tamaño de pantalla

## 🛠️ Características Técnicas

- **View Binding**: Gestión segura de vistas sin findViewById
- **Material Design**: Componentes de Material Design 3
- **ConstraintLayout**: Sistema de layout flexible y eficiente
- **Orientación adaptativa**: Layouts específicos para cada orientación
- **Kotlin**: Desarrollado completamente en Kotlin
- **Arquitectura limpia**: Código bien estructurado y mantenible

## 📱 Requisitos del Sistema

- **Android Studio**: Versión 2023.1.1 o superior
- **JDK**: Versión 17 o superior
- **Gradle**: Versión 8.2
- **Android SDK**: API 24+ (Android 7.0+)
- **Dispositivo**: Android 7.0 o superior

## 🎵 Tipos de Música de Fondo

1. **🌧️ Lluvia**: Sonido relajante de gotas de lluvia
2. **🔥 Fuego**: Crepitar de leña y fuego
3. **🌲 Bosque**: Sonidos de la naturaleza y pájaros
4. 🌊 Océano**: Olas del mar y brisa marina
5. **🔇 Ruido Blanco**: Sonido constante para concentración

## 🚀 Instalación y Configuración

### 1. Clonar el repositorio
```bash
git clone <url-del-repositorio>
cd cronobat
```

### 2. Abrir en Android Studio
- Abre Android Studio
- Selecciona "Open an existing Android Studio project"
- Navega hasta la carpeta del proyecto y selecciónala

### 3. Sincronizar el proyecto
- Android Studio detectará automáticamente que es un proyecto Gradle
- Haz clic en "Sync Now" cuando aparezca la notificación
- O ve a File → Sync Project with Gradle Files

### 4. Ejecutar la aplicación
- Conecta un dispositivo Android o inicia un emulador
- Haz clic en el botón "Run" (▶️) en la barra de herramientas
- Selecciona tu dispositivo/emulador y haz clic en "OK"

## 📁 Estructura del Proyecto

```
cronobat/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/cronobat/
│   │   │   │   └── MainActivity.kt          # Lógica principal de la app
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml    # Layout vertical
│   │   │   │   │   └── layout-land/
│   │   │   │   │       └── activity_main.xml # Layout horizontal
│   │   │   │   ├── values/
│   │   │   │   │   ├── strings.xml          # Textos de la app
│   │   │   │   │   ├── colors.xml           # Paleta de colores
│   │   │   │   │   └── themes.xml           # Temas de la app
│   │   │   │   └── drawable/                # Iconos y recursos gráficos
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   ├── build.gradle
│   └── proguard-rules.pro
├── gradle/
├── build.gradle
├── settings.gradle
├── gradle.properties
├── gradlew
├── gradlew.bat
└── README.md
```

## 🎮 Cómo Usar la Aplicación

### Modo Temporizador
1. **Establecer tiempo**: Desliza el SeekBar para establecer la duración
2. **Iniciar**: Presiona "Iniciar Temporizador"
3. **Pausar**: Presiona "Pausar" para detener temporalmente
4. **Reiniciar**: Presiona "Reiniciar" para volver a cero

### Modo Cronómetro
1. **Iniciar**: Presiona "Iniciar Cronómetro"
2. **Pausar**: Presiona "Pausar" para detener temporalmente
3. **Reiniciar**: Presiona "Reiniciar" para volver a cero

### Música de Fondo
1. **Iniciar música**: Presiona "Iniciar Música"
2. **Cambiar sonido**: Presiona "Cambiar Música" para alternar entre tipos
3. **Pausar**: Presiona "Pausar Música" para detener

### Cambiar Modo
- Presiona el botón "Cambiar a Cronómetro" o "Cambiar a Temporizador"
- La aplicación se reiniciará automáticamente

## 🎨 Personalización

### Cambiar Colores
Edita `app/src/main/res/values/colors.xml` para personalizar la paleta de colores:

```xml
<color name="primary_color">#2196F3</color>
<color name="accent_color">#FF4081</color>
<color name="background_color">#F5F5F5</color>
```

### Cambiar Textos
Edita `app/src/main/res/values/strings.xml` para personalizar los textos:

```xml
<string name="time_display_label">Tu Texto Personalizado</string>
```

### Cambiar Temas
Edita `app/src/main/res/values/themes.xml` para personalizar el tema de la aplicación.

## 🔧 Construcción y Distribución

### Build de Debug
```bash
./gradlew assembleDebug
```

### Build de Release
```bash
./gradlew assembleRelease
```

### Instalación en Dispositivo
```bash
./gradlew installDebug
```

## 🎵 Implementación de Música de Fondo

**Nota**: La funcionalidad de música de fondo está preparada en el código pero requiere implementación adicional:

1. **Agregar archivos de audio**: Coloca archivos MP3 en `app/src/main/res/raw/`
2. **Implementar MediaPlayer**: Usa MediaPlayer para reproducción de audio
3. **Permisos**: Agrega permisos de audio en AndroidManifest.xml
4. **Gestión de recursos**: Implementa liberación de recursos de audio

## 🐛 Solución de Problemas

### Error de Gradle Sync
- Verifica que tienes la versión correcta de JDK instalada
- Limpia el proyecto: Build → Clean Project
- Invalida caches: File → Invalidate Caches and Restart

### Error de Compilación
- Verifica que todas las dependencias están correctamente definidas
- Asegúrate de que el SDK de Android esté instalado
- Verifica la versión de Gradle en `gradle/wrapper/gradle-wrapper.properties`

### Error de Ejecución
- Verifica que el dispositivo/emulador esté funcionando correctamente
- Asegúrate de que la aplicación tenga los permisos necesarios
- Revisa los logs de Android Studio para más detalles

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 📞 Contacto

- **Desarrollador**: Tu Nombre
- **Email**: tu.email@ejemplo.com
- **Proyecto**: [https://github.com/tuusuario/cronobat](https://github.com/tuusuario/cronobat)

---

**Nota**: Esta aplicación está configurada para funcionar inmediatamente después de la clonación. Solo necesitas tener Android Studio y un dispositivo/emulador configurado para comenzar a desarrollar. La funcionalidad de música de fondo está preparada en el código pero requiere implementación adicional de archivos de audio y MediaPlayer.