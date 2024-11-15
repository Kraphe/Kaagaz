import React from 'react';
import { useNavigate } from 'react-router-dom';

const Premium = () => {
  const navigate = useNavigate();

  // Inline style for the glowing effect
  const glowingCardStyle: React.CSSProperties = {
    position: 'relative',
    borderRadius: '8px',
    animation: 'glowAnimation 1.5s infinite alternate',
  };

  // Handle subscription selection and redirection
  const handleSubscriptionClick = (subscriptionType: 'monthly' | 'yearly') => {
    navigate(`/payment/${subscriptionType}`); 
  };

  return (
    <div className="flex flex-col items-center min-h-screen bg-gray-100 p-8">
      {/* Title Section */}
      <div className="text-center mb-8">
        <h1 className="text-4xl font-bold text-gray-800">Premium</h1>
        <p className="text-gray-500 mt-2">Get started with a LeetCode Subscription that works for you.</p>
      </div>

      {/* Subscription Cards */}
      <div className="flex flex-col md:flex-row gap-8">
        {/* Monthly Plan Card */}
        <div className="bg-gray-50 shadow-lg rounded-lg p-8 w-96 h-96 flex flex-col justify-between">
          <h2 className="text-lg font-semibold text-gray-800">Monthly <span className="text-sm text-gray-500">billed monthly</span></h2>
          <p className="text-sm text-gray-500 mt-1">Down from $39/month.</p>
          <p className="text-gray-700 mt-2">Our monthly plan grants access to <span className="font-semibold">all premium features</span>, the best plan for short-term subscribers.</p>
          <div className="text-3xl font-bold text-gray-800 mt-6">$35 <span className="text-base font-normal">/mo</span></div>
          <p className="text-xs text-gray-500 mt-2">Prices are marked in USD</p>
          <button
            className="bg-black text-white mt-6 py-2 w-full rounded-md font-semibold"
            onClick={() => handleSubscriptionClick('monthly')}
          >
            Subscribe
          </button>
        </div>

        {/* Yearly Plan Card with Glowing Border */}
        <div
          className="bg-orange-100 shadow-lg p-8 w-96 h-96 flex flex-col justify-between"
          style={glowingCardStyle}  // Applying the inline style with glowing effect
        >
          <div className="flex items-center gap-2">
            <h2 className="text-lg font-semibold text-gray-800">Yearly <span className="text-sm text-gray-500">billed yearly ($159)</span></h2>
            <span className="bg-yellow-200 text-yellow-700 text-xs font-medium px-2 py-1 rounded-md">🎉 Most popular</span>
          </div>
          <p className="text-sm text-gray-500 mt-1">Our most popular plan previously sold for $299 and is now only $13.25/month.</p>
          <p className="text-gray-700 mt-2">This plan <span className="font-semibold">saves you over 60%</span> in comparison to the monthly plan.</p>
          <div className="text-3xl font-bold text-gray-800 mt-6">$13.25 <span className="text-base font-normal">/mo</span></div>
          <p className="text-xs text-gray-500 mt-2">Prices are marked in USD</p>
          <button
            className="bg-black text-white mt-6 py-2 w-full rounded-md font-semibold"
            onClick={() => handleSubscriptionClick('yearly')}
          >
            Subscribe
          </button>
        </div>
      </div>

      {/* Adding the CSS animation dynamically in the head */}
      <style>
        {`
          @keyframes glowAnimation {
            0% {
              box-shadow: 0 0 5px rgba(255, 165, 0, 0.8), 0 0 10px rgba(255, 165, 0, 0.6);
            }
            100% {
              box-shadow: 0 0 20px rgba(255, 165, 0, 1), 0 0 30px rgba(255, 165, 0, 0.8);
            }
          }
        `}
      </style>
    </div>
  );
};

export default Premium;
