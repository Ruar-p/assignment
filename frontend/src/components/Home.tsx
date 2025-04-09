import { Link } from "react-router-dom";

const Home = () => {
    return (
        <div className="home-container">
            <h2>Welcome to the App</h2>
            <p>You are successfully logged in!</p>
            <Link to="/login">Logout</Link>
        </div>
    );
};

export default Home;