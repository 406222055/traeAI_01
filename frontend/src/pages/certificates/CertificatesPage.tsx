import { useEffect, useMemo, useState } from 'react';
import { Button, Card, Col, Drawer, Form, Input, Popconfirm, Row, Select, Space, Table, Tag, Typography, message } from 'antd';
import type { PersonnelCertificate, Project, Vendor, PersonnelCertificateStatus } from '../../shared';
import { PERSONNEL_CERTIFICATE_TYPES } from '../../shared';
import {
  createPersonnelCertificate,
  deletePersonnelCertificate,
  fetchPersonnelCertificates,
  updatePersonnelCertificate,
} from '../../services/certificates';
import { fetchProjects } from '../../services/projects';
import { fetchVendors } from '../../services/vendors';

const statusTagColor = (status: PersonnelCertificateStatus) => {
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

const statusLabel = (status: PersonnelCertificateStatus) => {
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

const CERT_TYPE_LABEL: Record<string, string> = {
  safety_operation: '安全生产',
  special_operation: '特种作业',
  electrician: '电工证',
  welder: '焊工证',
  elevator: '电梯作业',
  crane: '起重机作业',
  scaffold: '架子工',
  high_altitude: '高处作业',
  other: '其他',
};

const certificateTypeLabel = (type: string) => CERT_TYPE_LABEL[type] || type;

export function CertificatesPage() {
  const [items, setItems] = useState<PersonnelCertificate[]>([]);
  const [vendors, setVendors] = useState<Vendor[]>([]);
  const [projects, setProjects] = useState<Project[]>([]);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState<PersonnelCertificate | null>(null);
  const [form] = Form.useForm();

  const load = async () => {
    try {
      const [list, vendorList, projectList] = await Promise.all([
        fetchPersonnelCertificates(),
        fetchVendors(),
        fetchProjects(),
      ]);
      setItems(list);
      setVendors(vendorList);
      setProjects(projectList);
    } catch (error) {
      message.error(error instanceof Error ? error.message : '加载人员证照失败');
    }
  };

  useEffect(() => {
    void load();
  }, []);

  const expired = useMemo(() => items.filter((c) => c.status === 'expired'), [items]);
  const expiringSoon = useMemo(() => items.filter((c) => c.status === 'expiring_soon'), [items]);

  return (
    <Row gutter={[16, 16]}>
      <Col span={12}>
        <Card
          title={
            <Space>
              <Typography.Text strong type="danger">
                已过期证照
              </Typography.Text>
              <Tag color="red">{expired.length}</Tag>
            </Space>
          }
          style={{ borderColor: '#ffccc7' }}
        >
          {expired.length === 0 ? (
            <Typography.Text type="secondary">暂无过期证照，保持良好！</Typography.Text>
          ) : (
            <Table
              rowKey="id"
              pagination={false}
              size="small"
              dataSource={expired}
              columns={[
                { title: '人员姓名', dataIndex: 'personnelName' },
                { title: '证照类型', dataIndex: 'certificateType', render: (v: string) => certificateTypeLabel(v) },
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
              <Typography.Text strong type="warning">
                即将到期（30 天内）
              </Typography.Text>
              <Tag color="orange">{expiringSoon.length}</Tag>
            </Space>
          }
          style={{ borderColor: '#ffe7ba' }}
        >
          {expiringSoon.length === 0 ? (
            <Typography.Text type="secondary">暂无即将到期证照</Typography.Text>
          ) : (
            <Table
              rowKey="id"
              pagination={false}
              size="small"
              dataSource={expiringSoon}
              columns={[
                { title: '人员姓名', dataIndex: 'personnelName' },
                { title: '证照类型', dataIndex: 'certificateType', render: (v: string) => certificateTypeLabel(v) },
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
          title="外协人员证照管理"
          extra={
            <Button
              type="primary"
              onClick={() => {
                setEditing(null);
                form.resetFields();
                form.setFieldsValue({ certificateType: 'safety_operation' });
                setOpen(true);
              }}
            >
              新增证照
            </Button>
          }
        >
          <Typography.Paragraph type="secondary">
            系统根据证照有效期自动计算状态：距到期 30 天内标记为"即将到期"，超过有效期标记为"已过期"。准入批准时若存在过期证照将被自动拦截。
          </Typography.Paragraph>
          <Table
            rowKey="id"
            dataSource={items}
            columns={[
              { title: '人员姓名', dataIndex: 'personnelName' },
              { title: '身份证号', dataIndex: 'idCardNo', render: (v: string | null) => v || '-' },
              { title: '证照类型', dataIndex: 'certificateType', render: (v: string) => certificateTypeLabel(v) },
              { title: '证照编号', dataIndex: 'certificateNo' },
              { title: '服务商', dataIndex: ['vendor', 'name'] },
              { title: '项目', dataIndex: ['project', 'name'], render: (v: string | undefined) => v || '-' },
              { title: '签发日期', dataIndex: 'issueDate' },
              { title: '到期日期', dataIndex: 'expiryDate' },
              {
                title: '状态',
                dataIndex: 'status',
                render: (value: PersonnelCertificateStatus) => (
                  <Tag color={statusTagColor(value)}>{statusLabel(value)}</Tag>
                ),
              },
              { title: '备注', dataIndex: 'remark', render: (v: string | null) => v || '-' },
              {
                title: '操作',
                render: (_, record) => (
                  <Space>
                    <Button
                      onClick={() => {
                        setEditing(record);
                        form.setFieldsValue({ ...record, projectId: record.projectId ?? undefined });
                        setOpen(true);
                      }}
                    >
                      编辑
                    </Button>
                    <Popconfirm
                      title="确认删除该证照？"
                      onConfirm={async () => {
                        try {
                          await deletePersonnelCertificate(record.id);
                          message.success('证照已删除');
                          await load();
                        } catch (error) {
                          message.error(error instanceof Error ? error.message : '删除证照失败');
                        }
                      }}
                    >
                      <Button danger>删除</Button>
                    </Popconfirm>
                  </Space>
                ),
              },
            ]}
          />
        </Card>
      </Col>
      <Drawer title={editing ? '编辑人员证照' : '新增人员证照'} open={open} onClose={() => setOpen(false)} width={520}>
        <Form
          layout="vertical"
          form={form}
          onFinish={async (values) => {
            try {
              if (editing) {
                await updatePersonnelCertificate(editing.id, values);
                message.success('证照已更新');
              } else {
                await createPersonnelCertificate(values);
                message.success('证照已创建');
              }
              setOpen(false);
              form.resetFields();
              await load();
            } catch (error) {
              message.error(error instanceof Error ? error.message : '保存证照失败');
            }
          }}
        >
          <Form.Item label="服务商" name="vendorId" rules={[{ required: true }]}>
            <Select options={vendors.map((item) => ({ label: item.name, value: item.id }))} />
          </Form.Item>
          <Form.Item label="所属项目" name="projectId">
            <Select allowClear options={projects.map((item) => ({ label: item.name, value: item.id }))} />
          </Form.Item>
          <Form.Item label="人员姓名" name="personnelName" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item label="身份证号" name="idCardNo">
            <Input />
          </Form.Item>
          <Form.Item label="证照类型" name="certificateType" rules={[{ required: true }]}>
            <Select
              options={PERSONNEL_CERTIFICATE_TYPES.map((value) => ({
                label: certificateTypeLabel(value),
                value,
              }))}
            />
          </Form.Item>
          <Form.Item label="证照编号" name="certificateNo" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item label="签发日期" name="issueDate" rules={[{ required: true }]}>
            <Input placeholder="2026-01-01" />
          </Form.Item>
          <Form.Item label="到期日期" name="expiryDate" rules={[{ required: true }]}>
            <Input placeholder="2027-01-01" />
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
