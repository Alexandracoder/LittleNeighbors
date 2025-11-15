import { useState, useEffect } from 'react';
import { familyService } from '../services/api';
import styles from './Profile.module.css';

const Profile = () => {
  const [family, setFamily] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchFamily();
  }, []);

  const fetchFamily = async () => {
    try {
      const response = await familyService.getAll(0, 1);
      if (response.data.content.length > 0) {
        setFamily(response.data.content[0]);
      }
    } catch (err) {
      setError('Failed to load profile');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className={styles.loading}>
        <div className={styles.spinner}></div>
        <p>Loading profile...</p>
      </div>
    );
  }

  if (error) {
    return <div className={styles.error}>{error}</div>;
  }

  if (!family) {
    return (
      <div className={styles.container}>
        <div className={styles.empty}>
          <h2>No Family Profile</h2>
          <p>Create a family profile to connect with neighbors</p>
          <button className={styles.createBtn}>Create Family Profile</button>
        </div>
      </div>
    );
  }

  return (
    <div className={styles.container}>
      <div className={styles.profileCard}>
        <div className={styles.profileHeader}>
          <div className={styles.profileImage}>
            {family.profilePictureUrl ? (
              <img src={family.profilePictureUrl} alt={family.familyName} />
            ) : (
              <div className={styles.placeholder}>👨‍👩‍👧‍👦</div>
            )}
          </div>

          <div className={styles.profileInfo}>
            <h1>{family.familyName}</h1>
            <p className={styles.representative}>{family.representativeName}</p>
            <div className={styles.location}>
              <span>📍</span>
              <span>{family.neighborhoodName}, {family.cityName}</span>
            </div>
          </div>

          <button className={styles.editBtn}>Edit Profile</button>
        </div>

        <div className={styles.section}>
          <h2>About Us</h2>
          <p>{family.description}</p>
        </div>

        {family.children && family.children.length > 0 && (
          <div className={styles.section}>
            <h2>Our Children</h2>
            <div className={styles.childrenGrid}>
              {family.children.map((child, idx) => (
                <div key={idx} className={styles.childCard}>
                  <div className={styles.childIcon}>
                    {child.gender === 'MALE' ? '👦' : '👧'}
                  </div>
                  <div className={styles.childInfo}>
                    <span className={styles.childAge}>{child.age} years old</span>
                    <span className={styles.childGender}>{child.gender}</span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Profile;
