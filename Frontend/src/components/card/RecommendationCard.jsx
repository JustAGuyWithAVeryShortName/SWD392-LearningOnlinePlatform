import { Card } from "react-bootstrap";
import "./RecommendationCard.css";

const RecommendationCard = ({ recommendation, type, onViewClick }) => {
  return (
    <Card
      className="recommendation-card"
      onClick={() => onViewClick(recommendation[`${type}ID`])}
    >
      <Card.Img
        variant="top"
        src={recommendation.image}
        alt={recommendation[`${type}Name`]}
        className="card-img-fixed"
      />

      <Card.Body className="card-body-fixed">
        <Card.Title className="card-title-clamp">
          {recommendation[`${type}Name`]}
        </Card.Title>

        <Card.Text className="card-text-clamp">
          {recommendation.description}
        </Card.Text>
      </Card.Body>
    </Card>
  );
};

export default RecommendationCard;