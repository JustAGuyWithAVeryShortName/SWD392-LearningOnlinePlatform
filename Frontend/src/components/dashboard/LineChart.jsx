import React, { useState, useMemo, useEffect } from 'react';
import { Card, Row, Col, ButtonGroup, Button, Form } from 'react-bootstrap';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';
import { Line } from 'react-chartjs-2';
import useFetch from '../../hooks/useFetch';
import { useTranslation } from 'react-i18next'; // Import useTranslation

// Đăng ký các thành phần cần thiết cho Chart.js
ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
);

const LineChart = () => {
  const { t } = useTranslation("lineChart"); // Use the new namespace

  const lineConfigs = useMemo(() => ({
    totalMembers: { color: 'var(--primary-hover)', name: t('totalMembers') },
    staffMembers: { color: '#10b981', name: t('lecturers') },
    consultants: { color: '#8b5cf6', name: t('customerSupport') },
    activeCourses: { color: '#ef4444', name: t('activeCourses') },
    blogs: { color: '#06b6d4', name: t('blogs') },
    events: { color: '#84cc16', name: t('events') },
    courses: { color: '#f97316', name: t('courses') },
    revenue: { color: '#16a34a', name: t('revenue') },
    paidCourses: { color: '#0ea5e9', name: t('paidCourses') }
  }), [t]);

  // State để lưu dữ liệu đã lọc sẽ được hiển thị trên biểu đồ
  const [filteredData, setFilteredData] = useState([]);
  const [activeFilter, setActiveFilter] = useState('THIS_YEAR');
  const [startedMonth, setStartedMonth] = useState('');
  const [endedMonth, setEndedMonth] = useState('');
  const [activeSeries, setActiveSeries] = useState([]);
  const { post: postData } = useFetch();

  useEffect(() => {
    setActiveSeries(Object.keys(lineConfigs));
  }, [lineConfigs]);

  useEffect(() => {
    const request = activeFilter === 'CUSTOM'
      ? { filterType: activeFilter, startedMonth, endedMonth }
      : { filterType: activeFilter };

    const fetchData = async () => {
      try {
        const resData = await postData(request, {}, 'http://localhost:8080/api/report');
        const safeData = Array.isArray(resData) ? resData : [];
        const processedData = safeData.map(item => ({
          ...item,
          date: item.date ? new Date(item.date) : null,
          revenue: Number(item.revenue ?? 0),
          paidCourses: Number(item.paidCourses ?? 0)
        }));
        setFilteredData(processedData);
      } catch (error) {
        console.error("Fetch error in LineChart:", error);
        setFilteredData([]);
      }
    };

    fetchData();
  }, [postData, activeFilter, startedMonth, endedMonth]);

  const handleFilterChange = (filterType) => {
    setActiveFilter(filterType);
  };

  const handleCustomDateFilter = () => {
    if (!startedMonth || !endedMonth) {
      alert(t('alertSelectDates'));
      return;
    }

    // Let the backend validate the custom range and return an empty list if invalid.
    setActiveFilter('CUSTOM');
  };

  const toggleSeries = (key) => {
    setActiveSeries((prev) => (
      prev.includes(key) ? prev.filter((item) => item !== key) : [...prev, key]
    ));
  };

  // Sử dụng useMemo để chỉ tính toán lại dữ liệu biểu đồ khi `filteredData` thay đổi.
  // Đây là một kỹ thuật tối ưu hóa quan trọng.
  const chartData = useMemo(() => {
    const visibleEntries = Object.entries(lineConfigs).filter(([key]) => activeSeries.includes(key));

    return {
      labels: filteredData.map(d => d.month),
      datasets: visibleEntries.map(([key, config]) => ({
        label: config.name,
        data: filteredData.map(d => d[key]),
        borderColor: config.color,
        backgroundColor: config.color,
        pointBackgroundColor: config.color,
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: config.color,
      })),
    };
  }, [filteredData, lineConfigs, activeSeries]);

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { position: 'bottom', labels: { usePointStyle: true, boxWidth: 8, padding: 20 } },
      title: { display: true, text: t('interactiveSystemMetrics'), font: { size: 18, weight: 'bold' }, padding: { top: 10, bottom: 20 }, align: 'start' },
      tooltip: { mode: 'index', intersect: false, backgroundColor: '#fff', titleColor: '#333', bodyColor: '#666', borderColor: '#ddd', borderWidth: 1 },
    },
    scales: {
      y: { beginAtZero: true, grid: { color: '#e9ecef', borderDash: [2, 2] }, ticks: { color: '#6c757d' } },
      x: { grid: { display: false }, ticks: { color: '#495057', font: { weight: '500' } } },
    },
    interaction: { mode: 'index', intersect: false },
    elements: { line: { tension: 0.4 }, point: { radius: 5, hoverRadius: 7, borderWidth: 2 } }
  };

  const showNoDataState = activeFilter === 'CUSTOM' && filteredData.length === 0;

  return (
    <Card className="chart-container h-100">
      <Card.Body className="p-4">
        {/* === FILTER AREA === */}
        <div className="mb-4">
          <h5 className="mb-3">{t('timeFilter')}</h5>

          {/* Custom range filter */}
          <Row className="g-2 mb-3 align-items-end">
            <Col md={4}>
              <Form.Control
                type="date"
                value={startedMonth}
                onChange={e => setStartedMonth(e.target.value)}
              />
            </Col>
            <Col md={4}>
              <Form.Control
                type="date"
                value={endedMonth}
                onChange={e => setEndedMonth(e.target.value)}
              />
            </Col>
            <Col md={4} className="d-grid">
              <Button variant={activeFilter === 'CUSTOM' ? 'primary' : 'outline-primary'} onClick={handleCustomDateFilter}>{t('apply')}</Button>
            </Col>
          </Row>

          {/* Quick filter */}
          <Row>
            <Col>
              <span className="me-2 fw-medium">{t('quickFilter')}</span>
              <ButtonGroup size="sm" className="me-3 mb-2">
                <Button variant={activeFilter === 'Q1' ? 'primary' : 'outline-secondary'} onClick={() => handleFilterChange('Q1')}>{t('q1')}</Button>
                <Button variant={activeFilter === 'Q2' ? 'primary' : 'outline-secondary'} onClick={() => handleFilterChange('Q2')}>{t('q2')}</Button>
                <Button variant={activeFilter === 'Q3' ? 'primary' : 'outline-secondary'} onClick={() => handleFilterChange('Q3')}>{t('q3')}</Button>
                <Button variant={activeFilter === 'Q4' ? 'primary' : 'outline-secondary'} onClick={() => handleFilterChange('Q4')}>{t('q4')}</Button>
              </ButtonGroup>
              <ButtonGroup size="sm" className="me-3 mb-2">
                <Button variant={activeFilter === 'FIRST_HALF' ? 'primary' : 'outline-secondary'} onClick={() => handleFilterChange('FIRST_HALF')}>{t('firstHalf')}</Button>
                <Button variant={activeFilter === 'LAST_HALF' ? 'primary' : 'outline-secondary'} onClick={() => handleFilterChange('LAST_HALF')}>{t('secondHalf')}</Button>
              </ButtonGroup>
              <ButtonGroup size="sm" className="mb-2">
                <Button variant={activeFilter === 'THIS_YEAR' ? 'primary' : 'outline-secondary'} onClick={() => handleFilterChange('THIS_YEAR')}>{t('thisYear')}</Button>
                <Button variant={activeFilter === 'ALL' ? 'primary' : 'outline-secondary'} onClick={() => handleFilterChange('ALL')}>{t('allTime')}</Button>
              </ButtonGroup>
            </Col>
          </Row>

          <Row>
            <Col>
              <span className="me-2 fw-medium">{t('series')}</span>
              {Object.entries(lineConfigs).map(([key, config]) => (
                <Button
                  key={key}
                  size="sm"
                  className="me-2 mb-2"
                  variant={activeSeries.includes(key) ? 'primary' : 'outline-secondary'}
                  onClick={() => toggleSeries(key)}
                  style={{ borderColor: config.color }}
                >
                  {config.name}
                </Button>
              ))}
            </Col>
          </Row>
        </div>

        <hr />

        {/* === CHART AREA === */}
        <div style={{ height: '320px' }}>
          {showNoDataState ? (
            <div className="h-100 d-flex align-items-center justify-content-center text-muted fw-medium">
              {t('noDataForSelectedRange')}
            </div>
          ) : (
            <Line options={chartOptions} data={chartData} />
          )}
        </div>
      </Card.Body>
    </Card>
  );
};

export default LineChart;