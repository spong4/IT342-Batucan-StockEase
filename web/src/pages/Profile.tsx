import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Profile: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [isEditing, setIsEditing] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  if (!user) {
    return (
      <div className="min-h-screen bg-gray-100 flex items-center justify-center">
        <p className="text-gray-600">Loading...</p>
      </div>
    );
  }

  const ownerPermissions = [
    { name: 'Manage staff', granted: user.role === 'OWNER' },
    { name: 'Manage products', granted: user.role === 'OWNER' },
    { name: 'View all sales', granted: user.role === 'OWNER' },
    { name: 'Record sales', granted: true },
    { name: 'View inventory', granted: true },
  ];

  return (
    <div className="min-h-screen bg-gray-100 py-12 px-4">
      <div className="max-w-2xl mx-auto bg-white rounded-lg shadow-lg p-8">
        {/* Header */}
        <div className="flex items-start justify-between mb-8 pb-8 border-b">
          <div className="flex items-start space-x-6">
            {/* Avatar */}
            <div className="bg-gray-300 rounded-full w-24 h-24 flex items-center justify-center">
              <svg className="w-12 h-12 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
            </div>

            {/* User Info */}
            <div>
              <h1 className="text-3xl font-bold text-gray-900">
                {user.firstname} {user.lastname}
              </h1>
              <p className="text-gray-600 mt-1">{user.email}</p>
              <span className="inline-block bg-green-100 text-green-800 px-3 py-1 rounded-full text-sm font-semibold mt-2">
                {user.role}
              </span>
            </div>
          </div>

          {/* Edit Profile Button */}
          <button
            onClick={() => setIsEditing(!isEditing)}
            className="px-6 py-2 bg-gray-200 text-gray-800 font-semibold rounded-lg hover:bg-gray-300 transition"
          >
            {isEditing ? 'Cancel' : 'Edit Profile'}
          </button>
        </div>

        {/* Profile Info */}
        {!isEditing ? (
          <>
            {/* Name & Email */}
            <div className="mb-8">
              <div className="grid grid-cols-2 gap-6 mb-6">
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-2">First Name</label>
                  <input
                    type="text"
                    value={user.firstname}
                    disabled
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg bg-gray-50 text-gray-700"
                  />
                </div>
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-2">Last Name</label>
                  <input
                    type="text"
                    value={user.lastname}
                    disabled
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg bg-gray-50 text-gray-700"
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">Email</label>
                <input
                  type="email"
                  value={user.email}
                  disabled
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg bg-gray-50 text-gray-700"
                />
              </div>
            </div>

            {/* Permissions */}
            <div className="mb-8 pb-8 border-b">
              <h2 className="text-lg font-bold text-gray-900 mb-4">Permissions</h2>
              <div className="space-y-3">
                {ownerPermissions.map((perm) => (
                  <div key={perm.name} className="flex items-center">
                    <div className={`w-5 h-5 rounded ${perm.granted ? 'bg-green-500' : 'bg-gray-300'}`}></div>
                    <span className="ml-3 text-gray-700">{perm.name}</span>
                  </div>
                ))}
              </div>
            </div>

            {/* Action Buttons */}
            <div className="grid grid-cols-2 gap-4">
              <button className="px-6 py-3 bg-gray-200 text-gray-800 font-semibold rounded-lg hover:bg-gray-300 transition">
                Change Password
              </button>
              <button
                onClick={handleLogout}
                className="px-6 py-3 bg-red-600 text-white font-semibold rounded-lg hover:bg-red-700 transition"
              >
                Logout
              </button>
            </div>
          </>
        ) : (
          <div className="bg-blue-50 p-4 rounded-lg">
            <p className="text-blue-800">Edit profile functionality would be implemented here</p>
          </div>
        )}

        {/* API Info */}
        <div className="bg-yellow-50 border border-yellow-200 rounded p-4 mt-8 text-xs">
          <p className="font-semibold text-gray-700">API: GET /auth/me, POST /auth/logout</p>
          <p className="text-gray-600">Security: Protected route, JWT required</p>
        </div>
      </div>
    </div>
  );
};

export default Profile;
