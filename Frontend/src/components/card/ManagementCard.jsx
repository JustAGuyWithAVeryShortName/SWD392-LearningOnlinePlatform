import React from "react";
import { PlusCircle } from "lucide-react";
import "./ManagementCard.css";

const ManagementCard = ({
  title,
  icon: IconComponent,
  iconBgClass,
  activeTab,
  setActiveTab,
  activeSubTab,
  setActiveSubTab,
  data,
  counts,
  dataType,
  onApprove,
  onEdit,
  onView,
  onAdd
}) => {

  const TabButton = ({ active, onClick, children, count }) => (
    <button
      className={`tab-button ${active ? "active" : ""}`}
      onClick={onClick}
    >
      <span>{children}</span>

      <span className={`tab-badge ${active ? "badge-active" : ""}`}>
        {count !== undefined ? count : "0"}
      </span>
    </button>
  );

  const SimpleCard = ({ item, isPending }) => {

    const name =
      item.courseName ||
      item.blogName ||
      item.eventName ||
      "No Title";

    const submittedDate = item.updatedAt || "recently";

    const id =
      item.courseID ||
      item.blogID ||
      item.eventID;

    const author =
      item.member?.username ||
      "Unknown Author";

    const type = dataType;

    return (
      <div className="item-card">

        <div className="item-left">

          <div className="item-title">
            {name}
          </div>

          <div className="item-meta">
            <span className="author">
              By {author}
            </span>

            <span className="dot">•</span>

            <span>
              {new Date(submittedDate).toLocaleDateString()} -
              {new Date(submittedDate).toLocaleTimeString([], {
                hour: "2-digit",
                minute: "2-digit"
              })}
            </span>

          </div>

        </div>

        <div className="item-actions">

          {item.blogID &&
            item.blogStatus === "PENDING" &&
            item.member.role !== "STAFF" && (

              <button
                className="btn approve"
                onClick={() =>
                  onApprove &&
                  onApprove(
                    id,
                    title.toLowerCase().slice(0, -1)
                  )
                }
              >
                Approve
              </button>

            )}

          {item.courseID && (
            <button
              className="btn edit"
              onClick={() => onEdit(id, type)}
            >
              Edit
            </button>
          )}

          <button
            className="btn view"
            onClick={() => onView(id, type)}
          >
            {isPending ? "Review" : "View"}
          </button>

        </div>

      </div>
    );
  };

  const itemsToRender =
    dataType === "blog"
      ? data?.[activeTab]?.[activeSubTab] || []
      : data?.[activeTab] || [];

  const renderContent = () => {

    if (itemsToRender.length === 0) {
      return (
        <div className="empty-text">
          No items to display.
        </div>
      );
    }

    return itemsToRender.map((item) => (
      <SimpleCard
        key={item.id || item.courseID || item.blogID}
        item={item}
        isPending={
          activeTab === "pending" ||
          activeSubTab === "pending"
        }
      />
    ));

  };

  return (
    <div className="management-card">

      {/* Header */}

      <div className="card-header">

        <div className="header-left">

          <div className={`icon-box ${iconBgClass}`}>
            <IconComponent size={20} />
          </div>

          <h3 className="card-title">
            {title}
          </h3>

        </div>

        {onAdd && (
          <button
            className="add-button"
            onClick={onAdd}
          >
            <PlusCircle size={16} />
            Add
          </button>
        )}

      </div>


      {/* Tabs */}

      {dataType === "blog" ? (

        <div className="tabs">

          <div className="tab-row">

            <TabButton
              active={activeTab === "me"}
              onClick={() => setActiveTab("me")}
              count={counts.me.pending + counts.me.approved}
            >
              Me
            </TabButton>

            <TabButton
              active={activeTab === "others"}
              onClick={() => setActiveTab("others")}
              count={
                counts.others.pending +
                counts.others.approved
              }
            >
              Others
            </TabButton>

          </div>

          <div className="tab-row sub">

            <TabButton
              active={activeSubTab === "pending"}
              onClick={() => setActiveSubTab("pending")}
              count={counts[activeTab]?.pending}
            >
              Pending
            </TabButton>

            <TabButton
              active={activeSubTab === "approved"}
              onClick={() => setActiveSubTab("approved")}
              count={counts[activeTab]?.approved}
            >
              Approved
            </TabButton>

          </div>

        </div>

      ) : (

        <div className="tab-row">

          <TabButton
            active={activeTab === "pending"}
            onClick={() => setActiveTab("pending")}
            count={counts?.pending}
          >
            Pending
          </TabButton>

          <TabButton
            active={activeTab === "approved"}
            onClick={() => setActiveTab("approved")}
            count={counts?.approved}
          >
            Approved
          </TabButton>

        </div>

      )}


      {/* Content */}

      <div className="card-content">
        {renderContent()}
      </div>

    </div>
  );
};

export default ManagementCard;