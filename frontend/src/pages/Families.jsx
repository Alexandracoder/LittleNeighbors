import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { familyService } from '../services/api';
import styles from './Families.module.css';

const Families = () => {
  const [families, setFamilies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    fetchFamilies();
  }, [page]);

  const fetchFamilies = async () => {
    setLoading(true);
    try {
      const response = await familyService.getAll(page, 9);
      setFamilies(response.data.content);
      setTotalPages(response.data.totalPages);
    } catch (err) {
      setError('Failed to load families');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className={styles.loading}>
        <div className={styles.spinner}></div>
        <p>Loading families...</p>
      </div>
    );
  }

  if (error) {
    return <div className={styles.error}>{error}</div>;
  }

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <h1>Families in Your Neighborhood</h1>
        <Link to="/profile/create-family" className={styles.createBtn}>
          Create Family Profile
        </Link>
      </div>

      {families.length === 0 ? (
        <div className={styles.empty}>
          <p>No families found. Be the first to create a family profile!</p>
        </div>
      ) : (
        <>
          <div className={styles.grid}>
            {families.map((family) => (
              <div key={family.id} className={styles.card}>
                <div className={styles.cardImage}>
                  {family.profilePictureUrl ? (
                    <img src={family.profilePictureUrl} alt={family.familyName} />
                  ) : (
                    <div className={styles.placeholder}>👨‍👩‍👧‍👦</div>
                  )}
                </div>

                <div className={styles.cardContent}>
                  <h3>{family.familyName}</h3>
                  <p className={styles.representative}>{family.representativeName}</p>

                  <div className={styles.location}>
                    <span>📍</span>
                    <span>{family.neighborhoodName}, {family.cityName}</span>
                  </div>

                  <p className={styles.description}>
                    {family.description?.substring(0, 100)}
                    {family.description?.length > 100 ? '...' : ''}
                  </p>

                  {family.children && family.children.length > 0 && (
                    <div className={styles.children}>
                      <span className={styles.childrenLabel}>Children:</span>
                      {family.children.map((child, idx) => (
                        <span key={idx} className={styles.childBadge}>
                          {child.gender === 'MALE' ? '👦' : '👧'} {child.age}y
                        </span>
                      ))}
                    </div>
                  )}

                  <Link to={`/families/${family.id}`} className={styles.viewBtn}>
                    View Profile
                  </Link>
                </div>
              </div>
            ))}
          </div>

          {totalPages > 1 && (
            <div className={styles.pagination}>
              <button
                onClick={() => setPage(p => Math.max(0, p - 1))}
                disabled={page === 0}
                className={styles.pageBtn}
              >
                Previous
              </button>

              <span className={styles.pageInfo}>
                Page {page + 1} of {totalPages}
              </span>

              <button
                onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
                disabled={page >= totalPages - 1}
                className={styles.pageBtn}
              >
                Next
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default Families;
