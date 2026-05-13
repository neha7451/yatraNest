# yatraNest
a hotel managment system
A full-stack travel and hotel booking platform built with the MERN stack. YatraNest allows users to discover destinations, book accommodations, and manage their travel plans seamlessly.

Features
User Authentication — Secure signup/login with JWT-based authentication
Hotel Listings — Browse hotels with detailed information, photos, and amenities
Search & Filter — Find accommodations by location, dates, price range, and ratings
Booking System — Reserve rooms with date selection and guest management
User Dashboard — View and manage bookings, profile settings, and booking history
Responsive Design — Optimized for desktop, tablet, and mobile devices
Tech Stack
Layer	Technology
Frontend	React, React Router, Axios
Backend	Node.js, Express.js
Database	MongoDB with Mongoose ODM
Authentication	JSON Web Tokens (JWT)
Styling	CSS / Tailwind CSS
Project Structure


yatraNest/
├── client/                 # React frontend
│   ├── public/
│   └── src/
│       ├── components/     # Reusable UI components
│       ├── pages/          # Route-level pages
│       ├── context/        # React context for state management
│       └── utils/          # Helper functions
├── server/                 # Express backend
│   ├── controllers/        # Request handlers
│   ├── models/             # Mongoose schemas
│   ├── routes/             # API route definitions
│   ├── middleware/         # Auth and validation middleware
│   └── config/             # Database and environment config
└── README.md
Getting Started
Prerequisites
Node.js (v16 or higher)
MongoDB (local installation or MongoDB Atlas)
npm or yarn
Installation
Clone the repository
bash


git clone [github.com](https://github.com/neha7451/yatraNest.git)
cd yatraNest
Install server dependencies
bash


cd server
npm install
Install client dependencies
bash


cd ../client
npm install
Configure environment variables Create a .env file in the /server directory:
env


PORT=5000
MONGODB_URI=your_mongodb_connection_string
JWT_SECRET=your_jwt_secret_key
Run the application Start the backend server:
bash


cd server
npm start
In a new terminal, start the frontend:
bash


cd client
npm start
Open your browser and navigate to [localhost](http://localhost:3000)
API Endpoints
Method	Endpoint	Description
POST	/api/auth/register	Register a new user
POST	/api/auth/login	User login
GET	/api/hotels	Get all hotels
GET	/api/hotels/:id	Get hotel by ID
POST	/api/bookings	Create a booking
GET	/api/bookings/user	Get user's bookings
Contributing
Contributions are welcome! Feel free to open issues or submit pull requests.

Fork the repository
Create a feature branch (git checkout -b feature/new-feature)
Commit your changes (git commit -m 'Add new feature')
Push to the branch (git push origin feature/new-feature)
Open a Pull Request
License
This project is open source and available under the MIT License [blocked].
