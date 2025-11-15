# Little Neighbors Frontend

A modern React application for connecting neighborhood families and organizing play dates.

## Features

- User authentication (register/login)
- Browse families in your neighborhood
- View family profiles with children information
- Manage your family profile
- Responsive design with beautiful UI

## Tech Stack

- React 18
- Vite
- React Router DOM
- Axios
- CSS Modules

## Color Palette

- Primary: #ffcf53 (Yellow)
- Secondary: #f6854d (Orange)

## Setup

1. Install dependencies:
```bash
npm install
```

2. Start the development server:
```bash
npm run dev
```

The app will be available at http://localhost:5173

## Backend Integration

The frontend expects the backend API to be running at `http://localhost:8080/api`

Ensure your Spring Boot backend is running before using the frontend.

## Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build

## Environment Variables

Create a `.env.local` file:

```
VITE_API_URL=http://localhost:8080/api
```
