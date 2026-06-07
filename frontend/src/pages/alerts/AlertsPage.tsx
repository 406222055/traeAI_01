import { useEffect, useState } from 'react';
import { Button, Card, Col, Drawer, Form, Input, Row, Select, Table, Tag, message } from 'antd';
import type { ComplianceItem, ExpiringAlertsResponse, PersonnelCertificate, Project, Vendor } from '../../shared';
import { COMPLIANCE_ITEM_STATUSES, COMPLIANCE_ITEM_TYPES } from '../../shared';
import { createComplianceItem, fetchComplianceItems, fetchExpiringAlerts, updateComplianceItem } from '../../services/alerts';
import { fetchProjects } from '../../services/projects';
import { fetchVendors } from '../../services/vendors';

export function AlertsPage() {
  const [items, setItems] = useState<ComplianceItem[]>([]);
  const [alerts, setAlerts] = useState<ExpiringAlertsResponse>({
    within7Days: [],
    within30Days: [],
    personnelWithin7Days: [],
    personnelWithin30Days: [],
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
        <Card title="7 天内到期 - 资质/合同">
          <Table
            rowKey="id"
            pagination={false}
            size="small"
            dataSource={alerts.within7Days}
            columns={[
              { title: '名称', dataIndex: 'name' },
              { title: '服务商', dataIndex: ['vendor', 'name'] },
              { title: '到期日期', dataIndex: 'expiryDate' },
            ]}
          />
        </Card>
      </Col>
      <Col span={12}>
        <Card title="7 天内到期 - 人员证照">
          <Table
            rowKey="id"
            pagination={false}
            size="small"
            dataSource={alerts.personnelWithin7Days}
            columns={[
              { title: '人员姓名', dataIndex: 'personnelName' },
              { title: '证照编号', dataIndex: 'certificateNo' },
              { title: '服务商', dataIndex: ['vendor', 'name'] },
              { title: '到期日期', dataIndex: 'expiryDate' },
            ]}
          />
        </Card>
      </Col>
      <Col span={12}>
        <Card title="30 天内到期 - 资质/合同">
          <Table
            rowKey="id"
            pagination={false}
            size="small"
            dataSource={alerts.within30Days}
            columns={[
              { title: '名称', dataIndex: 'name' },
              { title: '服务商', dataIndex: ['vendor', 'name'] },
              { title: '到期日期', dataIndex: 'expiryDate' },
            ]}
          />
        </Card>
      </Col>
      <Col span={12}>
        <Card title="30 天内到期 - 人员证照">
          <Table
            rowKey="id"
            pagination={false}
            size="small"
            dataSource={alerts.personnelWithin30Days}
            columns={[
              { title: '人员姓名', dataIndex: 'personnelName' },
              { title: '证照编号', dataIndex: 'certificateNo' },
              { title: '服务商', dataIndex: ['vendor', 'name'] },
              { title: '到期日期', dataIndex: 'expiryDate' },
            ]}
          />
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
                form.setFieldsValue({ status: 'active', type: 'qualification' });
                setOpen(true);
              }}
            >
              新增到期项
            </Button>
          }
        >
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
                render: (value: string) => <Tag color={value === 'active' ? 'green' : 'red'}>{value}</Tag>,
              },
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
          <Form.Item label="状态" name="status" rules={[{ required: true }]}>
            <Select options={COMPLIANCE_ITEM_STATUSES.map((value) => ({ label: value, value }))} />
          </Form.Item>
          <Form.Item label="备注" name="remark">
            <Input.TextArea rows={4} />
          </Form.Item>
          <Button type="primary" htmlType="submit" block>
            保存
          </Button>
        </Form>
      </Drawer>
    </Row>
  );
}
