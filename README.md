# Proyecto App: LEXA

La app tiene dos funciones principales:

- Un Chat con IA para hacer preguntas legales.
- Un Mapa para encontrar abogados.

## Funcionalidades Terminadas

1. Chat con IA
- Está conectado a la API de Google Gemini (usando el modelo gemini-2.5-flash) con Retrofit
- Las conversaciones se guardan en el teléfono usando una base de datos Room
- Puedes tener múltiples chats, crear chats nuevos y cargar los antiguos

2. Mapa de Abogados
- Usa la API de Google Maps para mostrar un mapa
- La lista de abogados se carga en tiempo real desde Cloud Firestore
- Pinta pines personalizados en el mapa para cada abogado

Aun hay detalles pendientes para la seccion del mapa y para la futura seccion del foro

