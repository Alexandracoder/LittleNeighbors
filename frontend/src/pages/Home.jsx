import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import styles from './Home.module.css';

const Home = () => {
  const { isAuthenticated } = useAuth();

  return (
    <div className={styles.home}>
      <section className={styles.hero}>
        <div className={styles.heroContent}>
          <h1 className={styles.title}>Welcome to Little Neighbors</h1>
          <p className={styles.subtitle}>
            Connect with families in your neighborhood and organize play dates for your children
          </p>

          <div className={styles.features}>
            <div className={styles.feature}>
              <div className={styles.featureIcon}>👨‍👩‍👧‍👦</div>
              <h3>Connect Families</h3>
              <p>Meet families in your area with children of similar ages</p>
            </div>

            <div className={styles.feature}>
              <div className={styles.featureIcon}>🎨</div>
              <h3>Share Interests</h3>
              <p>Find children who share your child's hobbies and interests</p>
            </div>

            <div className={styles.feature}>
              <div className={styles.featureIcon}>🏘️</div>
              <h3>Build Community</h3>
              <p>Create lasting friendships in your neighborhood</p>
            </div>
          </div>

          <div className={styles.cta}>
            {isAuthenticated ? (
              <Link to="/families" className={styles.ctaBtn}>
                Browse Families
              </Link>
            ) : (
              <>
                <Link to="/register" className={styles.ctaBtn}>
                  Get Started
                </Link>
                <Link to="/login" className={styles.ctaBtnSecondary}>
                  Login
                </Link>
              </>
            )}
          </div>
        </div>
      </section>
    </div>
  );
};

export default Home;
