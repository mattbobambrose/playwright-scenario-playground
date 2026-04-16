import express from 'express';
import cookieParser from 'cookie-parser';
import { sessionMiddleware } from './session/middleware.js';

const app = express();

// ---------------------------------------------------------------------------
// Middleware
// ---------------------------------------------------------------------------

app.use(express.urlencoded({ extended: true }));
app.use(cookieParser());
app.use(sessionMiddleware);

// ---------------------------------------------------------------------------
// Routes
// ---------------------------------------------------------------------------

import staticRoutes from './routes/static.js';
import catalogRoutes from './routes/catalog.js';
import cartRoutes from './routes/cart.js';
import checkoutRoutes from './routes/checkout.js';
import authRoutes from './routes/auth.js';
import shelvesRoutes from './routes/shelves.js';
import ordersRoutes from './routes/orders.js';

app.use('/', staticRoutes);
app.use('/', catalogRoutes);
app.use('/', cartRoutes);
app.use('/', checkoutRoutes);
app.use('/', authRoutes);
app.use('/', shelvesRoutes);
app.use('/', ordersRoutes);

export default app;
