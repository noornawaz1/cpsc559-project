import {useState} from "react";

interface AddTaskModalProps {
  addTask: (taskName: string) => void;
}

function AddTaskModal(props: AddTaskModalProps) {
  const [ input, setInput ] = useState("");

  return (
      <dialog>
        <h2>Enter task name:</h2>
        <form onSubmit={() => props.addTask(input)}>
          <input
            value={input}
            onChange={(e) => setInput(e.target.value)}
          />
          <button type="submit">Create task</button>
        </form>
      </dialog>
  );
}

export default AddTaskModal;
