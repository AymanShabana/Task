import React, { useEffect, useState } from 'react';
import { useAuth } from '../contexts/AuthContext';

const ListPage = () => {
  const { jwt } = useAuth();
  const [items, setItems] = useState([]);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const limit = 10;

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch(`http://backend:8080/orders?size=${limit}&page=${page}`, {
          headers: {
            'Authorization': `Bearer ${jwt}`
          }
        });
        if (response.ok) {
          const data = await response.json();
          setItems(data.items);
          setTotalPages(Math.ceil(data.total / limit));
        } else {
          throw new Error('Failed to fetch data');
        }
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };

    if (jwt) {
      fetchData();
    }
  }, [jwt, page]);

  const handleNextPage = () => {
    if (page < totalPages) {
      setPage(page + 1);
    }
  };

  const handlePreviousPage = () => {
    if (page > 1) {
      setPage(page - 1);
    }
  };

  return (
    <div>
      <h1>List Page</h1>
      <ul>
        {items.map((item, index) => (
          <li key={index}>{item}</li>
        ))}
      </ul>
      <div>
        <button onClick={handlePreviousPage} disabled={page === 1}>
          Previous
        </button>
        <span>{` Page ${page} of ${totalPages} `}</span>
        <button onClick={handleNextPage} disabled={page === totalPages}>
          Next
        </button>
      </div>
    </div>
  );
};

export default ListPage;
