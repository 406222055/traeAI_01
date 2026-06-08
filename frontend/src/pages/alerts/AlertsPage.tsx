import { useEffect, useState } from 'react';
import { Button, Card, Col, Drawer, Form, Input, Row, Select, Space, Table, Tag, Typography, message } from 'antd';
import type { ComplianceItem, ExpiringAlertsResponse, PersonnelCertificate, Project, Vendor } from '../../shared';
import { COMPLIANCE_ITEM_TYPES } from '../../shared';
import { createComplianceItem, fetchComplianceItems, fetchExpiringAlerts, updateComplianceItem } from '../../services/alerts';
import { fetchProjects } from '../../services/projects';
import { fetchVendors } from '../../services/vendors';

const statusTagColor = (status: string) => {
  switch (status) {
    case 'active':
      return 'green';
    case 'expiring_soon':
      return 'orange';
    case 'expired':
      return 'red';
    default:
      return 'default';
  }
};

const statusLabel = (status: string) => {
  switch (status) {
    case 'active':
      return '有效';
    case 'expiring_soon':
      return '即将到期(30天内)';
    case 'expired':
      return '已过期';
    default:
      return status;
  }
};

export function AlertsPage() {
  const [items, setItems] = useState<ComplianceItem[]>([]);
  const [alerts, setAlerts] = useState<ExpiringAlertsResponse>({
    expiredItems: [],
    expiringSoonItems: [],
    expiredPersonnelCerts: [],
    expiringSoonPersonnelCerts: [],
  });
  const [vendors, setVendors] = useState<Vendor[]>([]);
  const [projects, setProjects] = useState<Project[]>([]);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState<ComplianceItem | null>(null);
  const [form] = Form.useForm();

  const load = async () => {
    try {
      const [list, expiring, vendorList, projectList] = await Promise.all([
        fetchComplianceItems(),
        fetchExpiringAlerts(),
        fetchVendors(),
        fetchProjects(),
      ]);
      setItems(list);
      setAlerts(expiring);
      setVendors(vendorList);
      setProjects(projectList);
    } catch (error) {
      message.error(error instanceof Error ? error.message : '加载到期预警失败');
    }
  };

  useEffect(() => {
    void load();
  }, []);

  return (
    <Row gutter={[16, 16]}>
      <Col span={12}>
        <Card
          title={
            <Space>
              <Typography.Text strong type="danger">已过期 - 资质/合同</Typography.Text>
              <Tag color="red">{alerts.expiredItems.length}</Tag>
            </Space>
          }
          style={{ borderColor: '#ffccc7' }}
        >
          {alerts.expiredItems.length === 0 ? (
            <Typography.Text type="secondary">暂无已过期资质/合同</Typography.Text>
          ) : (
            <Table
              rowKey="id"
              pagination={false}
              size="small"
              dataSource={alerts.expiredItems}
              columns={[
                { title: '名称', dataIndex: 'name' },
                { title: '类型', dataIndex: 'type' },
                { title: '服务商', dataIndex: ['vendor', 'name'] },
                { title: '到期日期', dataIndex: 'expiryDate' },
              ]}
            />
          )}
        </Card>
      </Col>
      <Col span={12}>
        <Card
          title={
            <Space>
              <Typography.Text strong type="danger">已过期 - 人员证照</Typography.Text>
              <Tag color="red">{alerts.expiredPersonnelCerts.length}</Tag>
            </Space>
          }
          style={{ borderColor: '#ffccc7' }}
        >
          {alerts.expiredPersonnelCerts.length === 0 ? (
            <Typography.Text type="secondary">暂无已过期人员证照</Typography.Text>
          ) : (
            <Table
              rowKey="id"
              pagination={false}
              size="small"
              dataSource={alerts.expiredPersonnelCerts}
              columns={[
                { title: '人员姓名', dataIndex: 'personnelName' },
                { title: '证照编号', dataIndex: 'certificateNo' },
                { title: '服务商', dataIndex: ['vendor', 'name'] },
                { title: '到期日期', dataIndex: 'expiryDate' },
              ]}
            />
          )}
        </Card>
      </Col>
      <Col span={12}>
        <Card
          title={
            <Space>
              <Typography.Text strong type="warning">即将到期（30 天内） - 资质/合同</Typography.Text>
              <Tag color="orange">{alerts.expiringSoonItems.length}</Tag>
            </Space>
          }
          style={{ borderColor: '#ffe7ba' }}
        >
          {alerts.expiringSoonItems.length === 0 ? (
            <Typography.Text type="secondary">暂无即将到期资质/合同</Typography.Text>
          ) : (
            <Table
              rowKey="id"
              pagination={false}
              size="small"
              dataSource={alerts.expiringSoonItems}
              columns={[
                { title: '名称', dataIndex: 'name' },
                { title: '类型', dataIndex: 'type' },
                { title: '服务商', dataIndex: ['vendor', 'name'] },
                { title: '到期日期', dataIndex: 'expiryDate' },
              ]}
            />
          )}
        </Card>
      </Col>
      <Col span={12}>
        <Card
          title={
            <Space>
              <Typography.Text strong type="warning">即将到期（30 天内） - 人员证照</Typography.Text>
              <Tag color="orange">{alerts.expiringSoonPersonnelCerts.length}</Tag>
            </Space>
          }
          style={{ borderColor: '#ffe7ba' }}
        >
          {alerts.expiringSoonPersonnelCerts.length === 0 ? (
            <Typography.Text type="secondary">暂无即将到期人员证照</Typography.Text>
          ) : (
            <Table
              rowKey="id"
              pagination={false}
              size="small"
              dataSource={alerts.expiringSoonPersonnelCerts}
              columns={[
                { title: '人员姓名', dataIndex: 'personnelName' },
                { title: '证照编号', dataIndex: 'certificateNo' },
                { title: '服务商', dataIndex: ['vendor', 'name'] },
                { title: '到期日期', dataIndex: 'expiryDate' },
              ]}
            />
          )}
        </Card>
      </Col>
      <Col span={24}>
        <Card
          title="资质/合同到期管理"
          extra={
            <Button
              type="primary"
              onClick={() => {
                setEditing(null);
                form.resetFields();
                form.setFieldsValue({ type: 'qualification' });
                setOpen(true);
              }}
            >
              新增到期项
            </Button>
          }
        >
          <Typography.Paragraph type="secondary">
            状态由系统根据有效期自动计算：距到期 30 天内标记为"即将到期"，超过有效期标记为"已过期"。
          </Typography.Paragraph>
          <Table
            rowKey="id"
            dataSource={items}
            columns={[
              { title: '名称', dataIndex: 'name' },
              { title: '类型', dataIndex: 'type' },
              { title: '服务商', dataIndex: ['vendor', 'name'] },
              { title: '项目', dataIndex: ['project', 'name'], render: (value: string | undefined) => value || '-' },
              { title: '签发日期', dataIndex: 'issueDate' },
              { title: '到期日期', dataIndex: 'expiryDate' },
              {
                title: '状态',
                dataIndex: 'status',
                render: (value: string) => (
                  <Tag color={statusTagColor(value)}>{statusLabel(value)}
                </Tag>
              },
              { title: '备注', dataIndex: 'remark', render: (value: string | null) => value || '-' },
              {
                title: '操作',
                render: (_, record) => (
                  <Button
                    onClick={() => {
                      setEditing(record);
                      form.setFieldsValue({ ...record, projectId: record.projectId ?? undefined });
                      setOpen(true);
                    }}
                  >
                    编辑
                  </Button>
                ),
              },
            ]}
          />
        </Card>
      </Col>
      <Drawer title={editing ? '编辑到期项' : '新增到期项'} open={open} onClose={() => setOpen(false)} width={520}>
        <Form
          layout="vertical"
          form={form}
          onFinish={async (values) => {
            try {
              if (editing) {
                await updateComplianceItem(editing.id, values);
                message.success('到期项已更新');
              } else {
                await createComplianceItem(values);
                message.success('到期项已创建');
              }
              setOpen(false);
              form.resetFields();
              await load();
            } catch (error) {
              message.error(error instanceof Error ? error.message : '保存到期项失败');
            }
          }}
        >
          <Form.Item label="服务商" name="vendorId" rules={[{ required: true }]}>
            <Select options={vendors.map((item) => ({ label: item.name, value: item.id }))} />
          </Form.Item>
          <Form.Item label="项目" name="projectId">
            <Select allowClear options={projects.map((item) => ({ label: item.name, value: item.id }))} />
          </Form.Item>
          <Form.Item label="类型" name="type" rules={[{ required: true }]}>
            <Select options={COMPLIANCE_ITEM_TYPES.map((value) => ({ label: value, value }))} />
          </Form.Item>
          <Form.Item label="名称" name="name" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item label="签发日期" name="issueDate" rules={[{ required: true }]}>
            <Input placeholder="2026-06-01" />
          </Form.Item>
          <Form.Item label="到期日期" name="expiryDate" rules={[{ required: true }]}>
            <Input placeholder="2026-06-30" />
          </Form.Item>
          <Form.Item label="备注" name="remark">
            <Input.TextArea rows={4} />
          </Form.Item>
          <Typography.Paragraph type="secondary" style={{ marginTop: 8 }}>
            状态由系统根据"到期日期"自动计算，无需手动设置。
          </Typography.Paragraph>
          <Button type="primary" htmlType="submit" block>
            保存
          </Button>
        </Form>
      </Drawer>
    </Row>
  );
}
