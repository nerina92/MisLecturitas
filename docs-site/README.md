# Documentación de Mis Lecturitas

Este sitio web está construido usando [Docusaurus](https://docusaurus.io/), un generador moderno de sitios estáticos.

## 📚 Contenido

La documentación incluye:

- **Introducción**: Visión general de la aplicación
- **Arquitectura**: Patrón MVVM y estructura del proyecto
- **Tecnologías**: Stack tecnológico completo (Kotlin, Compose, Firebase)
- **Firebase Auth**: Guía de implementación de autenticación
- **Instalación**: Configuración del entorno de desarrollo
- **Blog**: Actualizaciones y noticias del proyecto

## 🚀 Instalación

```bash
npm install
```

## 💻 Desarrollo local

```bash
npm start
```

Este comando inicia un servidor de desarrollo local y abre una ventana del navegador. La mayoría de los cambios se reflejan en vivo sin necesidad de reiniciar el servidor.

## 🏗️ Build

```bash
npm run build
```

Este comando genera contenido estático en el directorio `build` y puede ser servido usando cualquier servicio de hosting de contenido estático.

## 🌐 Deployment a GitHub Pages

```bash
npm run deploy
```

Este comando construye el sitio web y lo publica en la rama `gh-pages` de GitHub.

### Configuración

El sitio está configurado para ser desplegado en:
- **URL**: https://nerina92.github.io/MisLecturitas/
- **Organización**: nerina92
- **Proyecto**: MisLecturitas
- **Branch**: gh-pages

## 📝 Agregar documentación

### Nuevo documento

1. Crea un archivo `.md` en `docs/`
2. Agrega el frontmatter:
```markdown
---
sidebar_position: X
---

# Título del documento
```

### Nuevo post de blog

1. Crea un archivo en `blog/` con formato `YYYY-MM-DD-nombre.md`
2. Agrega el frontmatter:
```markdown
---
slug: nombre
title: Título del Post
authors: [nerina]
tags: [tag1, tag2]
---
```

## 🎨 Personalización

- **Config principal**: `docusaurus.config.js`
- **Sidebar**: `sidebars.js`
- **Página de inicio**: `src/pages/index.js`
- **CSS custom**: `src/css/custom.css`
- **Componentes**: `src/components/`

## 📦 Estructura

```
docs-site/
├── blog/                  # Posts de blog
├── docs/                  # Documentación
│   ├── intro.md
│   ├── arquitectura.md
│   ├── tecnologias.md
│   ├── firebase-auth.md
│   └── instalacion.md
├── src/
│   ├── components/       # Componentes React
│   ├── css/             # Estilos
│   └── pages/           # Páginas personalizadas
├── static/              # Archivos estáticos
└── docusaurus.config.js # Configuración
```

## 🔗 Links útiles

- [Documentación de Docusaurus](https://docusaurus.io/)
- [Repositorio del proyecto](https://github.com/nerina92/MisLecturitas)
- [Sitio web en vivo](https://nerina92.github.io/MisLecturitas/)
