import { useEffect, useState } from "react";
import { Appbar } from "../components/Appbar";
import { motion } from "framer-motion";
import axios from "axios";

export const Dashboard = () => {
  const [app, setApp] = useState(null); // Set initial state to null
  const [loading, setLoading] = useState(true); // Loading state

  const fetchHealthCheck = async () => {
    try {
      const res = await axios.get("http://localhost:8081/blog/health-check");
      setApp(res.data);
    } catch (error) {
      console.error("Error fetching health check:", error);
      // Optionally set an error state here
    } finally {
      setLoading(false); // Set loading to false after fetch attempt
    }
  };

  useEffect(() => {
    fetchHealthCheck();
  }, []);

  return (
    <div className="">
      <Appbar />

      <div className="h-screen w-full flex flex-col items-center justify-center text-center">
        <div className="mt-24 md:mt-24 overflow-y-hidden">
          <motion.div animate={{ y: [-100, 0] }} transition={{ duration: 1.1 }}>
            <h1 className="tracking-tighter text-4xl md:text-5xl lg:text-6xl xl:text-7xl mb-4 font-medium">
              Welcome to Kaagaz App {app}
            </h1>
          </motion.div>

          <motion.div animate={{ y: [100, 0] }} transition={{ duration: 1.1 }}>
            <h1 className="text-gray tracking-tighter md:text-md xl:text-xl ">
              Where your stories come to life, powered by Generative AI✨.
            </h1>
          </motion.div>
        </div>

        {loading ? ( // Conditional rendering based on loading state
          <div className="mt-4">Loading...</div>
        ) : (
          <div className="mb-2 h-80 w-80 md:h-72 md:w-72 ">
            <img
              className="h-full w-full object-cover"
              src="https://i.pinimg.com/originals/ec/90/0c/ec900cf378e31c147d2ae7b5fdba6ce6.gif"
              alt=""
            />
            <div>{app}</div> {/* Display fetched app data if needed */}
          </div>
        )}
      </div>
    </div>
  );
};
