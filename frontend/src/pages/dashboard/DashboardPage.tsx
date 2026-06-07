import { useEffect, useState } from 'react';
import { Card, Col, Row, Statistic, Typography, message } from 'antd';
import type { DashboardSummary } from '../../shared';
import { fetchDashboardSummary } from '../../services/dashboard';

export function DashboardPage() {
  const [summary, setSummary] = useState<DashboardSummary | null>(null);

  useEffect(() => {
    fetchDashboardSummary()
      .then(setSummary)
      .catch((error) => {
        message.error(error instanceof Error ? error.message : '加载统计失败');
      });
  }, []);

  return (
    <>
      <Typography.Title level={4}>首页统计</Typography.Title>
      <Row gutter={[16, 16]}>
        <Col span={8}>
          <Card><Statistic title="服务商总数" value={summary?.vendorCount ?? 0} /></Card>
        </Col>
        <Col span={8}>
          <Card><Statistic title="项目总数" value={summary?.projectCount ?? 0} /></Card>
        </Col>
        <Col span={8}>
          <Card><Statistic title="待审核准入" value={summary?.pendingAdmissionCount ?? 0} /></Card>
        </Col>
        <Col span={6}>
          <Card><Statistic title="7 天内资质到期" value={summary?.expiringIn7DaysCount ?? 0} /></Card>
        </Col>
        <Col span={6}>
          <Card><Statistic title="30 天内资质到期" value={summary?.expiringIn30DaysCount ?? 0} /></Card>
        </Col>
        <Col span={6}>
          <Card><Statistic title="7 天内证照到期" value={summary?.personnelCertExpiringIn7DaysCount ?? 0} valueStyle={{ color: '#cf1322' }} /></Card>
        </Col>
        <Col span={6}>
          <Card><Statistic title="30 天内证照到期" value={summary?.personnelCertExpiringIn30DaysCount ?? 0} valueStyle={{ color: '#fa8c16' }} /></Card>
        </Col>
      </Row>
    </>
  );
}
