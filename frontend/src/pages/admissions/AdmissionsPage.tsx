import { useEffect, useState } from 'react';
import { Button, Card, Drawer, Form, Input, Modal, Select, Space, Table, Tag, message } from 'antd';
import type { Admission, Project, Vendor } from '../../shared';
import { createAdmission, fetchAdmissions, reviewAdmission } from '../../services/admissions';
import { fetchProjects } from '../../services/projects';
import { fetchVendors } from '../../services/vendors';

export function AdmissionsPage() {
  const [items, setItems] = useState<Admission[]>([]);
  const [vendors, setVendors] = useState<Vendor[]>([]);
  const [projects, setProjects] = useState<Project[]>([]);
  const [open, setOpen] = useState(false);
  const [reviewing, setReviewing] = useState<Admission | null>(null);
  const [form] = Form.useForm();
  const [reviewForm] = Form.useForm();

  const load = async () => {
    try {
      const [admissions, vendorList, projectList] = await Promise.all([
        fetchAdmissions(),
        fetchVendors(),
        fetchProjects(),
      ]);
      setItems(admissions);
      setVendors(vendorList);
      setProjects(projectList);
    } catch (error) {
      message.error(error instanceof Error ? error.message : '加载准入申请失败');
    }
  };

  useEffect(() => {
    void load();
  }, []);

  return (
    <Card
      title="准入申请与审核"
      extra={
        <Button
          type="primary"
          onClick={() => {
            form.resetFields();
            setOpen(true);
          }}
        >
          发起准入申请
        </Button>
      }
    >
      <Table
        rowKey="id"
        dataSource={items}
        columns={[
          { title: '服务商', dataIndex: ['vendor', 'name'] },
          { title: '项目', dataIndex: ['project', 'name'] },
          { title: '申请日期', dataIndex: 'applyDate' },
          { title: '计划进场日期', dataIndex: 'plannedEntryDate' },
          { title: '工作范围', dataIndex: 'scopeOfWork' },
          { title: '状态', dataIndex: 'status', render: (value: string) => <Tag color={value === 'approved' ? 'green' : value === 'rejected' ? 'red' : 'gold'}>{value}</Tag> },
          { title: '审核意见', dataIndex: 'reviewComment', render: (value: string | null) => value || '-' },
          {
            title: '操作',
            render: (_, record) => (
              <Space>
                <Button
                  disabled={record.status !== 'pending'}
                  onClick={() => {
                    setReviewing(record);
                    reviewForm.resetFields();
                  }}
                >
                  审核
                </Button>
              </Space>
            ),
          },
        ]}
      />
      <Drawer title="发起准入申请" open={open} onClose={() => setOpen(false)} width={520}>
        <Form
          layout="vertical"
          form={form}
          onFinish={async (values) => {
            try {
              await createAdmission(values);
              message.success('准入申请已创建');
              setOpen(false);
              form.resetFields();
              await load();
            } catch (error) {
              message.error(error instanceof Error ? error.message : '创建准入申请失败');
            }
          }}
        >
          <Form.Item label="服务商" name="vendorId" rules={[{ required: true }]}>
            <Select options={vendors.map((item) => ({ label: item.name, value: item.id }))} />
          </Form.Item>
          <Form.Item label="项目" name="projectId" rules={[{ required: true }]}>
            <Select options={projects.map((item) => ({ label: item.name, value: item.id }))} />
          </Form.Item>
          <Form.Item label="申请日期" name="applyDate" rules={[{ required: true }]}><Input placeholder="2026-06-06" /></Form.Item>
          <Form.Item label="计划进场日期" name="plannedEntryDate" rules={[{ required: true }]}><Input placeholder="2026-06-10" /></Form.Item>
          <Form.Item label="工作范围" name="scopeOfWork" rules={[{ required: true }]}><Input.TextArea rows={4} /></Form.Item>
          <Button type="primary" htmlType="submit" block>提交申请</Button>
        </Form>
      </Drawer>
      <Modal
        title="审核准入申请"
        open={Boolean(reviewing)}
        onCancel={() => setReviewing(null)}
        footer={null}
      >
        <Form
          layout="vertical"
          form={reviewForm}
          onFinish={async (values) => {
            if (!reviewing) {
              return;
            }

            try {
              await reviewAdmission(reviewing.id, values);
              message.success('审核完成');
              setReviewing(null);
              reviewForm.resetFields();
              await load();
            } catch (error) {
              message.error(error instanceof Error ? error.message : '审核失败');
            }
          }}
        >
          <Form.Item label="审核结果" name="status" rules={[{ required: true }]}>
            <Select options={[{ label: 'approved', value: 'approved' }, { label: 'rejected', value: 'rejected' }]} />
          </Form.Item>
          <Form.Item label="审核意见" name="reviewComment"><Input.TextArea rows={4} /></Form.Item>
          <Button type="primary" htmlType="submit" block>提交审核</Button>
        </Form>
      </Modal>
    </Card>
  );
}
