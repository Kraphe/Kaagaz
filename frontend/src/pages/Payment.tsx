import axios from 'axios';
import  { useEffect, useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { BACKEND_URL_PREMIUM } from '../config';
import { useParams } from 'react-router-dom';


const Checkout = () => {
 const navigate = useNavigate();
  let {subscriptionType} = useParams();
  useEffect(()=>{
    if(subscriptionType!="yearly"&&subscriptionType!="monthly")
        {
          navigate(`/payment/yearly`);
        }
  })
  
  const [promoCode, setPromoCode] = useState('');
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [cardNumber, setCardNumber] = useState('');
  const [error, setError] = useState('');

  const amount= subscriptionType==="yearly"?1000:100;
  const handleCheckout = () => {
    if (!firstName || !lastName || !email || !cardNumber) {
      setError('All fields are required.');
      return;
    }
    
    if (!/^\d{12}$/.test(cardNumber)) {
      setError('Card number must be exactly 12 digits.');
      return;
    }
    axios.post(`${BACKEND_URL_PREMIUM}/subscription/subscribe`, {
        subscriptionType,
        email,
      }).then((res)=>{
        
        if(res.status==200){
            toast.success("Successfully availed premium")
            navigate("/blogs")
        }
      }).catch((err)=>{
        toast.error("No account found with this email address, craete account first") 
      })
    setError('');
    
  };

  return (
    <div className="flex flex-col items-center justify-center p-4 bg-gray-100 min-h-screen">
      <div className="bg-white shadow-lg rounded-lg w-full max-w-md p-6">
        <h2 className="text-lg font-semibold text-gray-800 mb-6">Checkout</h2>

        <div className="bg-blue-50 p-4 rounded-lg mb-4">
          <div className="flex items-center mb-2">
            <div className="bg-black text-white font-bold text-xs p-2 rounded-md mr-2">
              Premium
            </div>
            <div className="text-sm font-medium">
              {subscriptionType.charAt(0).toUpperCase() + subscriptionType.slice(1)} Subscription
            </div>
          </div>
          <div className="flex justify-between text-sm mb-1">
            <span>{amount}</span>
            <span>₹{amount.toFixed(2)}</span>
          </div>
          <div className="flex justify-between text-sm mb-1">
            <span>Est. Tax</span>
            <span>₹0.00</span>
          </div>
          <div className="flex items-center mt-2">
            <input
              type="text"
              placeholder="Enter code"
              value={promoCode}
              onChange={(e) => setPromoCode(e.target.value)}
              className="text-sm p-2 border border-gray-300 rounded-l-md w-full"
            />
            <button className="bg-blue-600 text-white px-3 py-2 rounded-r-md text-sm">
              Apply
            </button>
          </div>
          <div className="flex justify-between text-lg font-semibold mt-4">
            <span>{}</span>
            <span>₹{amount.toFixed(2)}</span>
          </div>
        </div>

        <h3 className="text-md font-medium text-gray-700 mb-2">Payment Method</h3>
        <select className="text-sm border border-gray-300 rounded-md w-full p-2 mb-4">
          <option>New Payment Method</option>
        </select>

        <div className="mb-4">
          <label className="block text-gray-600 text-sm mb-1">Please fill in your billing information</label>
          <div className="flex space-x-2">
            <input
              type="text"
              placeholder="First name"
              value={firstName}
              onChange={(e) => setFirstName(e.target.value)}
              className="text-sm p-2 border border-gray-300 rounded-md w-1/2"
            />
            <input
              type="text"
              placeholder="Last name"
              value={lastName}
              onChange={(e) => setLastName(e.target.value)}
              className="text-sm p-2 border border-gray-300 rounded-md w-1/2"
            />
          </div>
          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="text-sm p-2 border border-gray-300 rounded-md w-full mt-2"
          />
        </div>

        <div className="mb-4">
          <label className="block text-gray-600 text-sm mb-1">Please fill in your credit card information</label>
          <div className="flex items-center">
            <input
              type="text"
              placeholder="Card number"
              value={cardNumber}
              onChange={(e) => setCardNumber(e.target.value)}
              className="text-sm p-2 border border-gray-300 rounded-l-md w-full"
            />
            <button className="bg-green-600 text-white px-3 py-2 rounded-r-md text-sm ml-1">
              Autofill link
            </button>
          </div>
        </div>

        {error && (
          <div className="text-red-500 text-sm mb-4">
            {error}
          </div>
        )}

        <button
          onClick={handleCheckout}
          className="w-full bg-black text-white p-3 rounded-md font-semibold text-sm"
        >
          Checkout
        </button>
      </div>
    </div>
  );
};

export default Checkout;
